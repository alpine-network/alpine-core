/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.modrinth.minotaur.ModrinthExtension
import masecla.modrinth4j.model.version.ProjectVersion

plugins {
    id("com.modrinth.minotaur")
}

extensions.configure<ModrinthExtension> {
    val versionString: String
    val changelogContent: String
    if (isRelease()) {
        versionType.set(ProjectVersion.VersionType.RELEASE.name.lowercase())
        versionString = rootProject.version.toString()
        changelogContent = "See [GitHub release notes](https://github.com/alpine-network/alpine-core/releases/tag/$versionString)."
    } else {
        versionType.set(ProjectVersion.VersionType.BETA.name.lowercase())
        val runNumber = (System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1) + 60
        versionString = "${rootProject.version}+${runNumber}"
        val commitHash = latestCommitHash()
        changelogContent = "[$commitHash](https://github.com/alpine-network/alpine-core/commit/$commitHash) ${latestCommitMessage()}"
    }

    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("alpinecore")
    versionNumber.set(versionString)
    versionName.set(versionString)
    changelog.set(changelogContent)

    val mcVersions: List<String> = (property("modrinth_versions") as String)
        .split(',')
        .map { it.trim() }
    gameVersions.set(mcVersions)

    detectLoaders.set(false)
    autoAddDependsOn.set(false)
    dependencies {
        optional.project("placeholderapi")
    }

    uploadFile.set(tasks.named<ShadowJar>("shadowJar").get())
}

tasks {
    named("modrinth") {
        notCompatibleWithConfigurationCache("Minotaur limitation")
    }
}