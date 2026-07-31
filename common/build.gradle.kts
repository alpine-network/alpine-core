import org.gradle.api.attributes.java.TargetJvmVersion
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar

plugins {
    id("core.downgrade-conventions")
    id("core.blossom-conventions")
    id("core.api-floor-conventions")
}

mavenPublishing {
    coordinates(artifactId = "alpinecore")
    pom { name.set("AlpineCore") }
}

// region Publish the downgraded jar as the main artifact

tasks {
    // The pre-downgrade jar stays available: it is what you want when debugging a downgrade bug,
    // and it is the relink-capable form JvmDowngrader's LGPL terms ask us to distribute.
    jar {
        archiveClassifier.set("unshaded")
    }
    named<ShadeJar>("shadeDowngradedApi") {
        archiveClassifier.set("")
    }
}

// `components["java"]` takes its artifact from `jar`, which is now the un-downgraded one. Swap in
// the downgraded jar and correct the JVM-version attribute - without the attribute, a consumer on
// an older toolchain cannot resolve this module at all ("requires at least a Java N JVM").
val downgraded = tasks.named<ShadeJar>("shadeDowngradedApi")
listOf("apiElements", "runtimeElements").forEach { name ->
    configurations.named(name) {
        outgoing {
            artifacts.clear()
            artifact(downgraded)
        }
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
        }
    }
}

publishing.publications.withType<MavenPublication>().configureEach {
    artifact(tasks.named("jar")) { classifier = "unshaded" }
}

// endregion

dependencies {
    // Internal dependencies
    implementation(libs.localelib)
    implementation(libs.dbcp2)

    // Consumer dependencies
    api(platform(libs.adventure.bom))
    api(libs.adventure.api)
    api(libs.adventure.text.minimessage)
    api(libs.adventure.text.serializer.plain)
    api(libs.adventure.text.serializer.legacy)
    api(libs.annotations)
    api(libs.xseries)
    api(libs.litecommands.adventure)
    api(libs.configlib.yaml)
    api(libs.configlib.bukkit)
    api(libs.litecommands.bukkit)

    // 1.8.8 version floor
    compileOnly(libs.spigot.api) {
        exclude("junit")
        exclude("org.yaml", "snakeyaml")
    }

    // Server plugins
    compileOnly(libs.placeholderapi)
    compileOnly(libs.vault.api)

    // Testing dependencies
    testImplementation(libs.testng)
    testImplementation(libs.lang)
    // Needed at test runtime so ServiceLoader can actually instantiate a platform provider
    testImplementation(libs.spigot.api) {
        exclude("junit")
        exclude("org.yaml", "snakeyaml")
    }

    // Java 8 verification of the downgraded artifact. TestNG only: this source set compiles to
    // Java 8 bytecode, so its classpath cannot carry anything newer.
    "downgradeTestImplementation"(libs.testng)

    // Code generation
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("group", project.group.toString())
                property("name", "AlpineCore")
                property("version", project.version.toString())
            }
        }
    }
}

tasks {
    named("build") {
        dependsOn("javadoc")
    }
    test {
        useTestNG()
    }
    javadoc {
        val v = libs.versions
        applyLinks(
            "https://docs.oracle.com/en/java/javase/11/docs/api/",
            "https://hub.spigotmc.org/javadocs/spigot/",
            "https://lib.alpn.cloud/javadoc/alpine-public/dev/tomwmth/configlib/configlib-bukkit/${v.configlib.get()}/raw/",
            "https://lib.alpn.cloud/javadoc/alpine-public/dev/tomwmth/configlib/configlib-core/${v.configlib.get()}/raw/",
            "https://lib.alpn.cloud/javadoc/alpine-public/dev/tomwmth/configlib/configlib-yaml/${v.configlib.get()}/raw/",
            "https://repo.panda-lang.org/javadoc/releases/dev/rollczi/litecommands-bukkit/${v.litecommands.get()}/raw/",
        )
    }
}
