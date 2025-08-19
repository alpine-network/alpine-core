import java.text.SimpleDateFormat
import java.util.Date

/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
plugins {
    idea
    `java-library`
}

plugins.withId("java") {
    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

tasks {
    // Target Java 8
    withType<JavaCompile>().configureEach {
        options.release.set(8)
        configureOptions()
    }
    withType<Jar>().configureEach {
        configureManifest()
        includeLicense()
    }
    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.WARN
        filteringCharset = Charsets.UTF_8.name()
    }
    test {
        useTestNG()
    }
    javadoc {
        configureOptions()
    }
    register("writeVersionToFile") {
        group = "publishing"
        description = "Generates a file containing the project version."
        val v = version.toString()
        val f = rootProject.file(".version")
        doLast {
            f.writeText(v)
        }
    }
    named("clean") {
        delete(file("${rootProject.projectDir}/.version"))
    }
}

fun Jar.configureManifest() {
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Created-By" to "Gradle",
            "Built-JDK" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})",
            "Built-By" to System.getProperty("user.name"),
            "Built-Date" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
            "Name" to rootProject.group.toString().replace(".", "/"),
            "Implementation-Title" to rootProject.name,
            "Implementation-Version" to rootProject.version,
            "Implementation-Vendor" to "Crystal Development, LLC.",
            "Specification-Title" to rootProject.name,
            "Specification-Version" to rootProject.version,
            "Specification-Vendor" to "Crystal Development, LLC.",
        )
    }
}

fun Jar.includeLicense() {
    from(file("${rootProject.projectDir}/LICENSE")) {
        into("META-INF")
    }
    manifest {
        attributes(
            "License" to "MPL-2.0",
            "License-URL" to "https://mozilla.org/MPL/2.0/"
        )
    }
}

fun JavaCompile.configureOptions() {
    options.encoding = Charsets.UTF_8.name()
    options.compilerArgs.add("-parameters")
    options.compilerArgs.add("-Xlint:-options")
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
}

fun Javadoc.configureOptions() {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:all", "-quiet")
        docTitle("AlpineCore ${project.version}")
        charset(Charsets.UTF_8.name())
        encoding(Charsets.UTF_8.name())
        noTimestamp()
        use()

        val v = libs.versions
        links(
            "https://javadoc.io/doc/org.jetbrains/annotations/${v.annotations.get()}/",
            "https://javadoc.io/doc/commons-lang/commons-lang/${v.lang.get()}/",
            "https://javadoc.io/doc/com.google.code.gson/gson/${v.gson.get()}/",
            "https://javadoc.io/doc/com.google.guava/guava/${v.guava.get()}/",
            "https://javadoc.io/doc/com.github.cryptomorin/XSeries/${v.xseries.get()}/",
        )

        setOf(
            "api",
            "text-minimessage",
            "text-serializer-plain",
        ).forEach {
            links("https://jd.advntr.dev/${it}/${v.adventure.get()}")
        }

        setOf(
            "litecommands-adventure",
            "litecommands-adventure-platform",
            "litecommands-annotations",
            "litecommands-core",
            "litecommands-framework",
            "litecommands-programmatic",
        ).forEach {
            val base = "https://repo.panda-lang.org/javadoc/releases/dev/rollczi"
            links("${base}/${it}/${v.litecommands.get()}/raw/")
        }
    }
}