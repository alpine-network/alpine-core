/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.framework.command;

import co.crystaldev.alpinecore.AlpineCore;
import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.Activatable;
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author BestBearr
 * @since 0.3.0
 */
@AllArgsConstructor @Getter
public abstract class AlpineArgumentResolver<T> extends ArgumentResolver<CommandSender, T> implements Activatable {

    private final @NotNull Class<T> type;

    private final @Nullable String key;

    @Override
    public final void activate(@NotNull AlpinePlugin context) {
        // ArgumentResolvers should be shared across plugin instances
        AlpineCore.getInstance().registerArgumentResolver(context, this);
    }

    @Override
    public final void deactivate(@NotNull AlpinePlugin context) {
        AlpineCore.getInstance().unregisterArgumentResolvers(context);
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
