/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.ApacheNoticeResourceTransformer

plugins {
    id("core.base-conventions")
    id("com.gradleup.shadow")
}

components.named<AdhocComponentWithVariants>("java") {
    withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
        skip() // do not publish shaded jar to maven
    }
}

tasks {
    named<Jar>("jar") {
        archiveClassifier.set("unshaded")
    }
    named<ShadowJar>("shadowJar") {
        // https://gradleup.com/shadow/configuration/merging/#handling-duplicates-strategy
        duplicatesStrategy = DuplicatesStrategy.WARN
        failOnDuplicateEntries = true
        archiveClassifier.set("")

        configureRelocations()
        configureTransformers()
    }
    named("build") {
        dependsOn(named("shadowJar"))
    }
}

fun ShadowJar.configureRelocations() {
    val base = "${project.group}.${rootProject.name.lowercase()}.libs"
    // dbcp2
    relocate("org.apache.commons.dbcp2", "${base}.org.apache.commons.dbcp2")
    relocate("org.apache.commons.logging", "${base}.org.apache.commons.logging")
    relocate("org.apache.commons.pool2", "${base}.org.apache.commons.pool2")
    relocate("javax.transaction", "${base}.javax.transaction")
    // localelib
    relocate("me.pikamug.localelib", "${base}.localelib")
}

fun ShadowJar.configureTransformers() {
    mergeServiceFiles()

    // Exclude plugin.yml files from configlib & LocaleLib
    "plugin.yml".also {
        filesMatching(it) {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }

    // Satisfy Apache 2.0 license requirements.
    //  - Provide a copy of the license
    //  - Merge NOTICE files
    // See https://www.apache.org/legal/apply-license.html#license
    "META-INF/LICENSE.txt".also {
        // Note: This assumes all shaded 'LICENSE.txt' files are Apache 2.0.
        //       We need to update this if we shade other dependencies that
        //       provide a different license under the same path.
        exclude(it)
        from(project.rootProject.projectDir.resolve("gradle/licenses/apache-2.0.txt")) {
            into("META-INF/licenses")
            rename { "LICENSE-apache-dhcp2+pool2+logging.txt" }
        }
    }
    "META-INF/NOTICE.txt".also {
        filesMatching(it) {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE // Suppress warnings
        }
        transform(ApacheNoticeResourceTransformer::class.java) {
            addHeader.set(false)
            projectName = rootProject.name
            organizationName = "Crystal Development, LLC"
            organizationURL = "https://github.com/alpine-network"
        }
    }
}