package co.crystaldev.alpinecore;

import co.crystaldev.alpinecore.util.UuidTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import java.util.UUID;

/**
 * Global constants used in the plugin.
 *
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
public final class Reference {
    /** The name of the plugin */
    public static final String NAME = PluginInfo.NAME;
    /** The version of the plugin */
    public static final String VERSION = PluginInfo.VERSION;
    /** The maven group of the plugin */
    public static final String GROUP = PluginInfo.GROUP;

    /** A regular Gson parser */
    public static final Gson GSON = gsonBuilder().create();
    /** A pretty printing Gson parser */
    public static final Gson GSON_PRETTY = gsonBuilder().setPrettyPrinting().create();
    /** An Adventure Bukkit platform audience */
    public static final BukkitAudiences AUDIENCES = BukkitAudiences.create(AlpineCore.getInstance());

    private static GsonBuilder gsonBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(UUID.class, new UuidTypeAdapter())
                .disableHtmlEscaping();
    }
}
