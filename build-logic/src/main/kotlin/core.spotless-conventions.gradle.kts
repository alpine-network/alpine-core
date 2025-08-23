/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("com.diffplug.spotless")
}

extensions.configure<SpotlessExtension> {
    java {
        licenseHeaderFile(rootProject.file("gradle/licenses/mpl-2.0-header.txt"))
        targetExclude("**/generated/**")
        removeUnusedImports()
    }
}

tasks {
    named("build") {
        dependsOn(named("spotlessApply"))
    }
}