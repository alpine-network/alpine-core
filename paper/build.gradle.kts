import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.zip.ZipFile

plugins {
    id("core.java21-conventions")
    id("core.shadow-conventions")
    id("core.platform-conventions")
    id("core.distribution-conventions")
}

val bundled = configurations.create("bundled")
configurations.compileOnly { extendsFrom(bundled) }

dependencies {
    // The pre-downgrade Java 21 build
    bundled(project(mapOf("path" to ":alpinecore-common", "configuration" to "modernElements")))

    bundled(libs.litecommands.folia) { isTransitive = false }

    compileOnly(libs.paper.api.floor)

    // paper-api at the floor pulls Adventure 4 transitively. Adventure 5 is our API floor
    // regardless, so pin it rather than leaving it to version conflict resolution.
    compileOnly(platform(libs.adventure.bom))

    // Server plugins
    compileOnly(libs.placeholderapi)
    compileOnly(libs.vault.api)

    // Code generation
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

platform {
    provider.set("co.crystaldev.alpinecore.platform.paper.PaperPlatform")
}

distribution {
    platform.set("Paper")
    jar.set(tasks.named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
    loaders.set(listOf("paper", "folia"))
}

// Exposes the distributable to other projects - Modrinth uploads one version carrying both
// archives, and the loaders are a property of the version rather than of the file.
val distributionElements = configurations.consumable("distributionElements") {
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
    }
}

artifacts {
    add(distributionElements.name, tasks.named<ShadowJar>("shadowJar"))
}

// The published artifact is the fat jar, not `jar` (which shadow-conventions gives the `unshaded`
// classifier). Same swap the bukkit module makes; here there is no downgrade step in between.
listOf("apiElements", "runtimeElements").forEach { name ->
    configurations.named(name) {
        outgoing {
            artifacts.clear()
            artifact(tasks.named<ShadowJar>("shadowJar"))
        }
    }
}

// region Adventure must not be bundled

val verifyNoBundledAdventure = tasks.register("verifyNoBundledAdventure") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Asserts the Paper distributable bundles no Adventure classes."
    dependsOn(tasks.named("shadowJar"))
    val jarFile = tasks.named<ShadowJar>("shadowJar").flatMap { it.archiveFile }
    doLast {
        val zip = ZipFile(jarFile.get().asFile)
        zip.use { zip ->
            val offenders = zip.entries().asSequence()
                .map { it.name }
                .filter { it.startsWith("net/kyori/") && it.endsWith(".class") }
                .toList()
            require(offenders.isEmpty()) {
                "the Paper distributable must not bundle Adventure as Paper provides it. " +
                        "Found ${offenders.size} class(es), e.g. ${offenders.take(5)}"
            }
            println("adventure exclusion OK: 0 net/kyori classes")
        }
    }
}

tasks.named("check") {
    dependsOn(verifyNoBundledAdventure)
}

// endregion

tasks {
    shadowJar {
        configurations = listOf(bundled)
        archiveClassifier.set("")
        archiveFileName.set("AlpineCore-Paper-${project.version}.jar")

        dependencies {
            exclude(dependency("net.kyori:.*:.*"))
        }

        exclude("plugin.yml")
    }
    processResources {
        expandProperties("paper-plugin.yml")
    }
}
