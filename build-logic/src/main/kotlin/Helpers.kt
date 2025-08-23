/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.expand
import org.gradle.kotlin.dsl.getByType
import org.gradle.language.jvm.tasks.ProcessResources
import kotlin.text.trim

val Project.libs: LibrariesForLibs
    get() = rootProject.extensions.getByType()

fun Project.isRelease(): Boolean {
    return !project.version.toString().contains("-")
}

fun Project.executeGitCommand(vararg command: String): String {
    val result = providers.exec {
        commandLine = listOf("git") + command
    }.standardOutput.asText.get()
    return result.trim()
}

fun Project.latestCommitHash(): String {
    return executeGitCommand("rev-parse", "--short", "HEAD")
}

fun Project.latestCommitMessage(): String {
    return executeGitCommand("log", "-1", "--pretty=%B").trim()
}

fun ProcessResources.expandProperties(vararg files: String) {
    val n = project.rootProject.name
    val main = "${project.group}.${n.lowercase()}.${n}"
    val desc = project.description.toString()
    val ver = project.version.toString()
    val web = "https://github.com/alpine-network/alpine-core"
    filesMatching(files.toList()) {
        expand(
            "name" to n,
            "main" to main,
            "description" to desc,
            "version" to ver,
            "website" to web,
        )
    }
}

fun Javadoc.applyLinks(vararg links: String) {
    (options as StandardJavadocDocletOptions).apply {
        links(*links)
    }
}