/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

plugins {
    id("java-library")
}

// Bukkit types that changed between a class and an interface
val flippedTypes = mapOf(
    "org.bukkit.inventory.InventoryView" to "1.21 (class -> interface)",
)

val flippedTypeAllowlist = setOf(
    "PlatformInventories.java",
)

val verifyApiFloor = tasks.register("verifyApiFloor") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Fails if platform-agnostic sources reference a class/interface-flipped Bukkit type."

    val sourceRoot = layout.projectDirectory.dir("src/main/java")
    val forbidden = flippedTypes
    val allowlist = flippedTypeAllowlist

    inputs.dir(sourceRoot).withPropertyName("sources")
    outputs.upToDateWhen { true }

    doLast {
        val hits = mutableListOf<String>()
        listOf(sourceRoot.asFile).filter { it.exists() }.forEach { dir ->
            dir.walkTopDown()
                .filter { it.isFile && it.extension == "java" }
                .filter { it.name !in allowlist }
                .forEach { file ->
                    val text = file.readText()
                    forbidden.forEach { (type, note) ->
                        if (text.contains(type)) {
                            hits += "  ${file.name}: $type - flipped in $note"
                        }
                    }
                }
        }

        if (hits.isNotEmpty()) {
            throw GradleException(
                "Platform-agnostic sources must not reference class/interface-flipped Bukkit " +
                    "types; route them through the platform SPI instead:\n" +
                    hits.sorted().joinToString("\n")
            )
        }
    }
}

tasks.named("check") {
    dependsOn(verifyApiFloor)
}
