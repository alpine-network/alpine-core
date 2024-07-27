package co.crystaldev.alpinecore.util;

import co.crystaldev.alpinecore.AlpinePlugin;
import co.crystaldev.alpinecore.framework.config.AlpinePluginConfig;
import net.kyori.adventure.text.format.StyleBuilderApplicable;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BestBearr
 * @since 0.3.0
 */
public final class StyleTagResolver implements TagResolver {

    private final Map<String, Tag> styleToTagMap = new HashMap<>();

    private final AlpinePluginConfig config;

    public StyleTagResolver(@NotNull AlpinePlugin plugin) {
        this.config = plugin.getAlpineConfig();
    }

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        Map<String, String> styleToColor = this.config.styles;
        String style = styleToColor.get(name);
        if (style == null) {
            return null;
        }

        return this.styleToTagMap.computeIfAbsent(style, s -> {
            List<StyleBuilderApplicable> styles = Components.processStyle(s);
            return Tag.styling(b -> styles.forEach(b::apply));
        });
    }

    @Override
    public boolean has(@NotNull String name) {
        return this.config.styles.containsKey(name);
    }
}
