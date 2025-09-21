/*
 * This file is part of AlpineCore - https://github.com/alpine-network/alpine-core
 * Copyright (C) 2025 Crystal Development, LLC
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package co.crystaldev.alpinecore.handler;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.AlpinePluginConfig;
import co.crystaldev.alpinecore.framework.config.object.ConfigMessage;
import dev.rollczi.litecommands.argument.parser.ParserRegistry;
import dev.rollczi.litecommands.command.executor.CommandExecutor;
import dev.rollczi.litecommands.meta.Meta;
import dev.rollczi.litecommands.permission.PermissionService;
import dev.rollczi.litecommands.schematic.SchematicFormat;
import dev.rollczi.litecommands.schematic.SchematicInput;
import dev.rollczi.litecommands.schematic.SimpleSchematicGenerator;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BestBearr
 * @since 0.4.10
 */
public final class BaseSchematicGenerator extends SimpleSchematicGenerator<CommandSender> {

    private final AlpinePlugin plugin;

    public BaseSchematicGenerator(
            AlpinePlugin plugin,
            SchematicFormat format,
            PermissionService permissionService,
            ParserRegistry<CommandSender> parserRegistry
    ) {
        super(format, permissionService, parserRegistry);
        this.plugin = plugin;
    }

    @Override
    protected String generateExecutor(SchematicInput<CommandSender> input, CommandExecutor<CommandSender> executor) {
        String arguments = executor.getArguments().stream()
                .map(argument -> String.format(generateArgumentFormat(input, argument), generateArgumentName(input, argument)))
                .collect(Collectors.joining(" "));

        List<String> description = executor.meta().get(Meta.DESCRIPTION);

        AlpinePluginConfig.InvalidUsageMessages config = this.plugin.getAlpineConfig().invalidUsage;
        ConfigMessage format = description.isEmpty() ? config.argumentFormat : config.argumentDescriptionFormat;

        return format.buildString(this.plugin,
                "arguments", arguments,
                "description", description.isEmpty() ? "" : String.join(", ", description));
    }
}
