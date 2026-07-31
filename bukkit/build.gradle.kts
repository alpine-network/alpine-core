import org.gradle.api.attributes.java.TargetJvmVersion
import xyz.wagyourtail.jvmdg.gradle.task.DowngradeJar
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar

plugins {
    id("core.downgrade-conventions")
    id("core.shadow-conventions")
    id("core.platform-conventions")
    id("core.hangar-conventions")
    id("core.modrinth-conventions")
}

val bundled = configurations.create("bundled")
configurations.compileOnly { extendsFrom(bundled) }

// Type information for the downgrader only. Deliberately not on any compile classpath, so
// Paper-only API stays invisible to this module's sources.
val downgradeClasspath = configurations.create("downgradeClasspath") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    // The pre-downgrade Java 21 build; see `modernElements` in common/build.gradle.kts.
    bundled(project(mapOf("path" to ":alpinecore-common", "configuration" to "modernElements")))

    bundled(libs.litecommands.folia) { isTransitive = false }

    downgradeClasspath(libs.folia.scheduler.api) { isTransitive = false }
    downgradeClasspath(libs.protocollib) { isTransitive = false }
    // common compiles against Vault, and this module now downgrades common's classes rather than
    // consuming an already-downgraded jar - so the downgrader needs the types too.
    downgradeClasspath(libs.vault.api) { isTransitive = false }

    // The 1.8.8 floor
    compileOnly(libs.spigot.api) {
        exclude("junit")
        exclude("org.yaml", "snakeyaml")
    }

    // Java 8 verification of the downgraded distributable. TestNG only - this source set compiles
    // to Java 8 bytecode against the shaded jar, which already carries everything else it needs.
    "downgradeTestImplementation"(libs.testng)

    // Code generation
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

platform {
    provider.set("co.crystaldev.alpinecore.platform.bukkit.BukkitPlatform")
}

listOf("apiElements", "runtimeElements").forEach { name ->
    configurations.named(name) {
        outgoing {
            artifacts.clear()
            artifact(tasks.named<ShadeJar>("shadeDowngradedApi"))
        }
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
        }
    }
}

distribution {
    platform.set("Bukkit")
    jar.set(tasks.named<ShadeJar>("shadeDowngradedApi").flatMap { it.archiveFile })
    loaders.set(listOf("bukkit", "spigot", "paper", "purpur", "folia"))
}

tasks {
    shadowJar {
        configurations = listOf(bundled)
        archiveClassifier.set("bundled")
        destinationDirectory.set(layout.buildDirectory.dir("tmp/shadow"))
    }
    named<DowngradeJar>("downgradeJar") {
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        // litecommands-folia references the Folia scheduler API, which is on no compile classpath
        // here. Without it the downgrader logs "Could not find class ..." and falls back to
        // guessing the hierarchy when computing stack map frames.
        classpath += downgradeClasspath
    }
    // The distributable. archiveFileName controls the file on disk; the classifier still has to be
    // cleared, or it leaks into the published Maven artifact name.
    named<ShadeJar>("shadeDowngradedApi") {
        archiveClassifier.set("")
        archiveFileName.set("AlpineCore-Bukkit-${project.version}.jar")
        destinationDirectory.set(layout.buildDirectory.dir("libs"))
    }
    processResources {
        expandProperties("plugin.yml")
    }
}
