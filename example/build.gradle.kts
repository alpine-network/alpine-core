plugins {
    id("core.java21-conventions")
}

group = "dev.tomwmth.exampleplugin"
version = "1.0.0"

dependencies {
    compileOnly(projects.alpinecoreCommon)

    compileOnly(libs.spigot.api) {
        exclude("junit")
        exclude("org.yaml", "snakeyaml")
    }

    // Demonstrates AlpineIntegration against a third-party plugin
    compileOnly(libs.viaversion.api)

    // Code generation
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    jar {
        archiveFileName.set("ExamplePlugin-${project.version}.jar")
    }
    processResources {
        val pluginVersion = project.version.toString()
        filesMatching("plugin.yml") {
            expand("version" to pluginVersion)
        }
    }
    javadoc {
        enabled = false
    }
}
