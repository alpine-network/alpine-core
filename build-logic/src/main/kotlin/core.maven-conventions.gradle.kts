/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    id("com.vanniktech.maven.publish")
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "AlpineCloud"
            url = uri("https://lib.alpn.cloud" + if (isRelease()) "/releases" else "/snapshots")
            credentials {
                username = System.getenv("ALPINE_MAVEN_NAME")
                password = System.getenv("ALPINE_MAVEN_SECRET")
            }
        }
    }
}

extensions.configure(MavenPublishBaseExtension::class.java) {
    configure(
        JavaLibrary(
            javadocJar = JavadocJar.Javadoc(),
            sourcesJar = true
        )
    )
    coordinates(
        groupId = project.group.toString(),
        artifactId = "alpinecore",
        version = project.version.toString(),
    )
    val orgId = "alpine-network"
    val repoId = "alpine-core"
    val repoUrl = "https://github.com/${orgId}/${repoId}"

    pom {
        name.set("alpinecore")
        description.set(project.description)
        url.set(repoUrl)

        licenses {
            license {
                name.set("MPL 2.0")
                url.set("https://www.mozilla.org/en-US/MPL/2.0/")
                distribution.set("repo")
            }
        }

        organization {
            name.set(orgId)
            url.set("https://github.com/${orgId}")
        }

        developers {
            developer {
                id.set("BestBearr")
                name.set("BestBearr")
            }
            developer {
                id.set("tomwmth")
                name.set("Thomas Wearmouth")
                email.set("tomwmth(at)pm.me")
            }
        }

        scm {
            url.set(repoUrl)
            connection.set("scm:git:${repoUrl}.git")
            developerConnection.set("scm:git:git@github.com:${orgId}/${repoId}.git")
            tag.set(project.version.toString())
        }

        issueManagement {
            system.set("GitHub")
            url.set("${repoUrl}/issues")
        }
    }
}