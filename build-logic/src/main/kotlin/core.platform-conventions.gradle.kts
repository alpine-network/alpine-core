/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import java.util.zip.ZipFile

plugins {
    id("java-library")
}

val platform = extensions.create<AlpinePlatformExtension>("platform")

// region Platform service declaration

// Generated rather than checked in, so renaming or moving the implementation class can never
// silently orphan the service file.
val serviceEntry = "META-INF/services/co.crystaldev.alpinecore.platform.AlpinePlatform"
val servicesDir = layout.buildDirectory.dir("generated/services")

val generatePlatformService = tasks.register("generatePlatformService") {
    description = "Writes the META-INF/services entry for the bundled AlpinePlatform."
    // capture plain locals rather than referencing the script properties from inside the
    // action - the configuration cache cannot serialize script object references.
    val impl = platform.provider
    val output = servicesDir.map { it.file(serviceEntry) }
    inputs.property("impl", impl)
    outputs.file(output)
    doLast {
        output.get().asFile.apply {
            parentFile.mkdirs()
            writeText(impl.get() + "\n")
        }
    }
}

the<SourceSetContainer>().named("main") {
    resources.srcDir(generatePlatformService.map { servicesDir })
}

/** Guards against the service file going missing or naming a class that isn't a provider. */
val verifyPlatformService = tasks.register("verifyPlatformService") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Asserts the built jar declares exactly one valid AlpinePlatform provider."
    dependsOn(tasks.named("shadowJar"))
    val jarFile = tasks.named<Jar>("shadowJar").flatMap { it.archiveFile }
    val expected = platform.provider
    val entry = serviceEntry
    doLast {
        val zip = ZipFile(jarFile.get().asFile)
        zip.use { zip ->
            val found = zip.getEntry(entry)
                ?: throw GradleException("$entry is missing from the built jar")
            val declared = zip.getInputStream(found).reader().readText()
                .lines().map { it.trim() }.filter { it.isNotEmpty() && !it.startsWith("#") }
            require(declared == listOf(expected.get())) {
                "expected exactly one provider <${expected.get()}>, jar declares $declared"
            }
            println("service file OK: ${expected.get()}")
        }
    }
}

tasks.named("check") {
    dependsOn(verifyPlatformService)
}

// endregion
