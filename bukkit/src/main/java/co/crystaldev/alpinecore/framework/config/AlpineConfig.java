/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.config;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Activatable;
import de.exlll.configlib.Configuration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * A wrapper around ConfigLib to allow for
 * automatic activation by the framework.
 * <p>
 * Inheritors should never be manually instantiated.
 *
 * @see <a href="https://github.com/Exlll/ConfigLib/wiki">Exlll/ConfigLib</a>
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@NoArgsConstructor @Getter
@Configuration
public abstract class AlpineConfig implements Activatable {

    private transient boolean active;

    /**
     * Gets the name for the file that this configuration
     * should be saved to.
     * <p>
     * Inheritors must override this if they wish to change
     * the name from its default.
     *
     * @return The name of the file
     */
    public @NotNull String getFileName() {
        return this.getClass().getSimpleName().toLowerCase() + ".yml";
    }

    @Override
    public final void activate(@NotNull AlpinePlugin context) {
        context.getConfigManager().registerConfig(this);
        this.active = true;
    }

    @Override
    public final void deactivate(@NotNull AlpinePlugin context) {
        context.getConfigManager().unregisterConfig(this);
        this.active = false;
    }
}
