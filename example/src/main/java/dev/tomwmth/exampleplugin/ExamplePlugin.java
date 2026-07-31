/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2026 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.tomwmth.exampleplugin;

import co.crystaldev.alpinecore.AlpinePlugin;
import org.jetbrains.annotations.NotNull;

/**
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public class ExamplePlugin extends AlpinePlugin {
    @Override
    public void onStart() {
        // Put startup logic here
    }

    @Override
    public void onStop() {
        // Put shutdown logic here
    }

    @Override
    public void setupVariables(@NotNull VariableConsumer variableConsumer) {
        variableConsumer.addVariable("prefix", "<dark_gray>[</dark_gray><gradient:#e81cff:#40c9ff>Example</gradient><dark_gray>]</dark_gray>");
    }
}
