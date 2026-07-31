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

// region The variant the platform modules consume

// `apiElements`/`runtimeElements` now carry the downgraded Java 8 jar, which is right for the
// outside world and wrong for our own platform modules: each of them merges `common` into a fat
// jar and handles bytecode level itself - bukkit downgrades the merged result, paper ships Java 25.
// Consuming the downgraded jar would make bukkit downgrade twice and would leave paper shipping
// Java 8 classes plus a JvmDowngrader shim it has no use for.
val modernElements = configurations.consumable("modernElements") {
    extendsFrom(
        configurations.api.get(),
        configurations.implementation.get(),
        configurations.runtimeOnly.get(),
    )
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
        attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
    }
}

artifacts {
    add(modernElements.name, tasks.named<Jar>("jar"))
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

// region Modern API check

// Compiles the very same sources a second time, against paper-api instead of the 1.8.8 floor.
// `common` is compiled against spigot-api, so a method that Bukkit has since *removed* still
// resolves happily here and only fails on a modern server - at runtime, in front of users. This
// turns that into a compile error.
val modernCheck = sourceSets.create("modernCheck") {
    java.setSrcDirs(emptyList<File>())
    resources.setSrcDirs(emptyList<File>())
}

configurations.named(modernCheck.compileOnlyConfigurationName) {
    extendsFrom(
        configurations.api.get(),
        configurations.implementation.get(),
        configurations.compileOnly.get(),
    )
    // The whole point: swap the floor out for the modern API.
    exclude(group = "org.spigotmc", module = "spigot-api")
}

dependencies {
    add(modernCheck.compileOnlyConfigurationName, libs.paper.api)
    add(modernCheck.annotationProcessorConfigurationName, libs.lombok)
}

tasks.named<JavaCompile>(modernCheck.compileJavaTaskName) {
    description = "Compiles common's sources against paper-api to catch use of removed Bukkit API."
    // Passing the SourceDirectorySet rather than its directories keeps the dependency on blossom's
    // generated templates.
    source(sourceSets.main.get().java)

    // Legitimately un-compilable against a modern API: this file exists to translate numeric
    // material IDs, so it names Material constants and Material#getId, all removed in the 1.13
    // flattening. Every one of those references sits behind `!XReflection.supports(1, 13, 0)`, and
    // the JVM resolves field and method references lazily, so unreached code never links.
    //
    // Its callers still have to resolve it, so `main`'s output - compiled against the 1.8.8 floor -
    // goes on the classpath below. Only allowlisted files come from there; everything else is
    // compiled from source against paper-api, which is the entire point of this task.
    exclude("**/util/MaterialHelper.java")
    classpath += sourceSets.main.get().output

    // paper-api is org.gradle.jvm.version 25; javac cannot read class files newer than its release.
    options.release.set(25)
    // Nothing consumes the output - only whether it compiles.
    destinationDirectory.set(layout.buildDirectory.dir("classes/java/modernCheck"))
}

tasks.named("check") {
    dependsOn(tasks.named(modernCheck.compileJavaTaskName))
}

// endregion

// region Adventure floor check (advisory)

// The Paper distribution compiles against Adventure 5 but runs against whatever the server provides
val adventureFloorCheck = sourceSets.create("adventureFloorCheck") {
    java.setSrcDirs(emptyList<File>())
    resources.setSrcDirs(emptyList<File>())
}

configurations.named(adventureFloorCheck.compileOnlyConfigurationName) {
    extendsFrom(
        configurations.api.get(),
        configurations.implementation.get(),
        configurations.compileOnly.get(),
    )
}

configurations.named(adventureFloorCheck.compileClasspathConfigurationName) {
    val floor = libs.versions.adventureFloor.get()
    resolutionStrategy.force(
        "net.kyori:adventure-api:$floor",
        "net.kyori:adventure-key:$floor",
        "net.kyori:adventure-text-minimessage:$floor",
        "net.kyori:adventure-text-serializer-legacy:$floor",
        "net.kyori:adventure-text-serializer-plain:$floor",
    )
}

dependencies {
    add(adventureFloorCheck.annotationProcessorConfigurationName, libs.lombok)
}

tasks.named<JavaCompile>(adventureFloorCheck.compileJavaTaskName) {
    description = "Reports (does not fail on) use of Adventure API newer than the Paper floor provides."
    source(sourceSets.main.get().java)
    destinationDirectory.set(layout.buildDirectory.dir("classes/java/adventureFloorCheck"))

    // Advisory: report, don't block.
    options.isFailOnError = false

    val floor = libs.versions.adventureFloor.get()
    val paperFloor = libs.versions.paperFloor.get()
    doFirst {
        logger.lifecycle(
            "adventure floor check: compiling against Adventure $floor (what Paper $paperFloor " +
                "provides). Any 'error:' below means the Paper archive requires Paper 26.2+."
        )
    }
}

tasks.named("check") {
    dependsOn(tasks.named(adventureFloorCheck.compileJavaTaskName))
}

// endregion

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
