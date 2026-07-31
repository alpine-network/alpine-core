/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import java.util.zip.ZipFile
import xyz.wagyourtail.jvmdg.gradle.JVMDowngraderExtension
import xyz.wagyourtail.jvmdg.gradle.task.DowngradeJar
import xyz.wagyourtail.jvmdg.gradle.task.ShadeJar

plugins {
    id("core.java21-conventions")
    id("xyz.wagyourtail.jvmdowngrader")
}

extensions.configure<JVMDowngraderExtension> {
    downgradeTo = JavaVersion.VERSION_1_8

    // MUST be namespaced. Two plugins shipping the JVMDG runtime at its default
    // `xyz/wagyourtail/jvmdg/...` path will fight over the class loader on a shared server.
    shadePath = { _ -> "co/crystaldev/alpinecore/libs/jvmdowngrader" }
}

abstract class ExclusiveShadeService : BuildService<BuildServiceParameters.None>

val shadeLock = gradle.sharedServices.registerIfAbsent(
    "jvmdgShadeLock",
    ExclusiveShadeService::class.java
) {
    maxParallelUsages.set(1)
}

// core.base-conventions stamps our manifest attributes onto every Jar task, but these two rewrite
// an existing jar and carry its manifest over wholesale. Re-stamping therefore appends a second,
// identical section - which also pushes jvmdg's own `Multi-Release` attribute out of the main
// section, where it is the only place the JAR spec honors it.
tasks.withType<DowngradeJar>().configureEach {
    manifest.attributes.clear()
}
tasks.withType<ShadeJar>().configureEach {
    manifest.attributes.clear()
}

tasks {
    // Intermediate: Java 8 bytecode that still references the un-relocated JVMDG runtime.
    named<DowngradeJar>("downgradeJar") {
        archiveClassifier.set("downgraded")
        destinationDirectory.set(layout.buildDirectory.dir("tmp/downgrade"))
    }

    // Final: the same jar with only the JVMDG stubs it actually uses, inlined at our namespace.
    named<ShadeJar>("shadeDowngradedApi") {
        usesService(shadeLock)
        // jvmdowngrader#45: `shadePath` is a non-serializable lambda.
        notCompatibleWithConfigurationCache("jvmdowngrader#45: shadePath is not serializable")
    }

    named("assemble") {
        dependsOn(named("shadeDowngradedApi"))
    }
}

val verifyDowngrade = tasks.register("verifyDowngrade") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Asserts the downgraded artifact is entirely Java 8 bytecode."
    dependsOn(tasks.named("shadeDowngradedApi"))

    val jarFile = tasks.named<ShadeJar>("shadeDowngradedApi").flatMap { it.archiveFile }
    doLast {
        val tooNew = mutableListOf<String>()
        var total = 0
        var ourShim = 0
        var strayShim = 0

        val zip = ZipFile(jarFile.get().asFile)
        zip.use { zip ->
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val name = entry.getName()

                if (name.startsWith("co/crystaldev/alpinecore/libs/jvmdowngrader")) {
                    ourShim++
                }
                if (name.startsWith("xyz/wagyourtail")) {
                    strayShim++
                }
                if (!name.endsWith(".class")) {
                    continue
                }

                total++
                val stream = zip.getInputStream(entry)
                stream.use { stream ->
                    val header = ByteArray(8)
                    var read = 0
                    while (read < 8) {
                        val n = stream.read(header, read, 8 - read)
                        if (n < 0) break
                        read += n
                    }
                    val major = ((header[6].toInt() and 0xFF) shl 8) or (header[7].toInt() and 0xFF)
                    if (major > 52) {
                        tooNew += "  $name (major $major)"
                    }
                }
            }
        }

        if (tooNew.isNotEmpty()) {
            throw GradleException(
                "${tooNew.size} of $total classes are above Java 8 bytecode (major 52) and will " +
                    "throw UnsupportedClassVersionError on a Java 8 server:\n" +
                    tooNew.take(40).joinToString("\n")
            )
        }
        if (strayShim > 0) {
            throw GradleException(
                "$strayShim JVMDG runtime entries were left at the default xyz/wagyourtail path; " +
                    "they must be relocated so they cannot clash with other plugins"
            )
        }

        println("downgrade OK: $total classes <= major 52, $ourShim relocated JVMDG entries")
    }
}

tasks.named("check") {
    dependsOn(verifyDowngrade)
}

plugins.withId("java") {
    val sourceSets = extensions.getByType<SourceSetContainer>()

    // A dedicated source set, deliberately separate from `test`.
    //
    // These classes must themselves be Java 8 bytecode to load on a Java 8 JVM, which means their
    // compile classpath carries `TargetJvmVersion = 8` and can therefore only see Java-8-compatible
    // libraries. The main `test` source set links against modern ones (LiteCommands 3.11 is Java 17
    // bytecode), so the two cannot share a classpath. Keeping them apart is what lets each be honest
    // about what it targets.
    val shadedJar = tasks.named<ShadeJar>("shadeDowngradedApi").flatMap { it.archiveFile }

    val downgradeTest = sourceSets.create("downgradeTest") {
        compileClasspath = files(shadedJar) + configurations["downgradeTestCompileClasspath"]
        runtimeClasspath = output + files(shadedJar) + configurations["downgradeTestRuntimeClasspath"]
    }

    tasks.named<JavaCompile>(downgradeTest.compileJavaTaskName) {
        dependsOn(tasks.named("shadeDowngradedApi"))
        options.release.set(8)
    }

    val downgradedTest = tasks.register<Test>("downgradedTest") {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
        description = "Verifies the downgraded artifact on a real Java 8 JVM."

        val shaded = shadedJar

        useTestNG()
        jvmArgumentProviders.add(CommandLineArgumentProvider {
            listOf("-Dalpinecore.downgraded.jar=${shaded.get().asFile.absolutePath}")
        })
        testClassesDirs = downgradeTest.output.classesDirs
        // The shaded jar is the code under test
        classpath = files(shaded) + downgradeTest.output + configurations["downgradeTestRuntimeClasspath"]

        javaLauncher.set(
            project.extensions.getByType<JavaToolchainService>().launcherFor {
                languageVersion.set(JavaLanguageVersion.of(8))
            }
        )
    }

    tasks.named("check") {
        dependsOn(downgradedTest)
    }
}
