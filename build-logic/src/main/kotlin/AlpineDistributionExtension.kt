/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class AlpineDistributionExtension {
    /** The shippable archive for this platform. */
    abstract val jar: RegularFileProperty

    /** Modrinth loader ids this version supports. */
    abstract val loaders: ListProperty<String>

    /** Human-readable platform name. */
    abstract val platform: Property<String>

    /**
     * Further archives to attach to the same release.
     *
     * Modrinth scopes loaders and game versions to the *version*, not to each file, so the Bukkit
     * and Paper archives ship as one version with two files rather than as two versions.
     */
    abstract val additionalJars: ConfigurableFileCollection
}
