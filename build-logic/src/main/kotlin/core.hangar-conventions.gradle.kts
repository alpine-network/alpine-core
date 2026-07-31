/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import io.papermc.hangarpublishplugin.HangarPublishExtension

plugins {
    id("core.distribution-conventions")
    id("io.papermc.hangar-publish-plugin")
}

private val dist = extensions.getByType<AlpineDistributionExtension>()

extensions.configure<HangarPublishExtension> {
    publications {
        register("plugin") {
            val versionString: String = rootProject.version.toString()
            val changelogContent: String
            if (project.isRelease()) {
                channel.set("Release")
                version.set(versionString)
                changelogContent = "https://github.com/alpine-network/alpine-core/releases/tag/$versionString"
            } else {
                channel.set("Snapshot")
                changelogContent = latestCommitMessage()
                val runNumber = (System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1) + 60
                version.set("${versionString}+${runNumber}")
            }
            id.set("AlpineCore")
            changelog.set(changelogContent)
            apiKey.set(System.getenv("HANGAR_API_TOKEN"))
            platforms {
                paper {
                    jar.set(dist.jar)
                    platformVersions.set(listOf(property("hangar_versions") as String))
                    dependencies {
                        hangar("PlaceholderAPI") {
                            required.set(false)
                        }
                        url("VaultAPI", "https://github.com/MilkBowl/VaultAPI") {
                            required.set(false)
                        }
                    }
                }
            }
        }
    }
}

tasks {
    named("publishPluginPublicationToHangar") {
        notCompatibleWithConfigurationCache("")
    }
}