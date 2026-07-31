import java.util.zip.ZipFile
import org.gradle.api.attributes.java.TargetJvmVersion
import xyz.wagyourtail.jvmdg.gradle.task.DowngradeJar
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar

plugins {
    id("core.downgrade-conventions")
    id("core.shadow-conventions")
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
    bundled(projects.alpinecoreCommon)

    bundled(libs.litecommands.folia) { isTransitive = false }

    downgradeClasspath(libs.folia.scheduler.api) { isTransitive = false }
    downgradeClasspath(libs.protocollib) { isTransitive = false }

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

// region Platform service declaration

// Generated rather than checked in, so renaming or moving the implementation class can never
// silently orphan the service file.
val platformImpl = "co.crystaldev.alpinecore.platform.bukkit.BukkitPlatform"
val serviceEntry = "META-INF/services/co.crystaldev.alpinecore.platform.AlpinePlatform"
val servicesDir = layout.buildDirectory.dir("generated/services")
val generatePlatformService = tasks.register("generatePlatformService") {
    description = "Writes the META-INF/services entry for the bundled AlpinePlatform."
    // NB: capture plain locals rather than referencing the script properties from inside the
    // action - the configuration cache cannot serialize script object references.
    val impl = platformImpl
    val output = servicesDir.map { it.file(serviceEntry) }
    inputs.property("impl", impl)
    outputs.file(output)
    doLast {
        output.get().asFile.apply {
            parentFile.mkdirs()
            writeText(impl + "\n")
        }
    }
}

sourceSets {
    main {
        resources.srcDir(generatePlatformService.map { servicesDir })
    }
}

/** Guards against the service file going missing or naming a class that isn't a provider. */
val verifyPlatformService = tasks.register("verifyPlatformService") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Asserts the built jar declares exactly one valid AlpinePlatform provider."
    dependsOn(tasks.named("shadowJar"))
    val jarFile = tasks.named<org.gradle.jvm.tasks.Jar>("shadowJar").flatMap { it.archiveFile }
    val expected = platformImpl
    val entry = serviceEntry
    doLast {
        val zip = ZipFile(jarFile.get().asFile)
        try {
            val found = zip.getEntry(entry)
                ?: throw GradleException("$entry is missing from the built jar")
            val declared = zip.getInputStream(found).reader().readText()
                .lines().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("#") }
            require(declared == listOf(expected)) {
                "expected exactly one provider <$expected>, jar declares $declared"
            }
            println("service file OK: $expected")
        }
        finally {
            zip.close()
        }
    }
}

tasks.named("check") {
    dependsOn(verifyPlatformService)
}

// endregion

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
