package co.crystaldev.alpinecore.framework.config.object.item;

import com.cryptomorin.xseries.XMaterial;
import de.exlll.configlib.Serializer;

import java.util.*;

/**
 * @since 0.4.0
 */
abstract class ConfigItemAdapter implements Serializer<ConfigItem, Map<String, Object>> {

    public abstract boolean requiresType();

    @Override
    public Map<String, Object> serialize(ConfigItem element) {
        Map<String, Object> result = new LinkedHashMap<>();

        if (element instanceof DefinedConfigItem) {
            result.put("type", ((DefinedConfigItem) element).getType().name());
        }

        result.put("name", element.getName());

        if (element.getLore() != null && !element.getLore().isEmpty()) {
            result.put("lore", element.getLore());
        }

        if (element.isEnchanted()) {
            result.put("enchanted", element.isEnchanted());
        }

        if (element.getCount() > 0) {
            result.put("count", element.getCount());
        }

        if (element.getAttributes() != null && !element.getAttributes().isEmpty()) {
            result.put("attributes", element.getAttributes());
        }

        return result;
    }

    @Override
    public ConfigItem deserialize(Map<String, Object> element) {
        Object type = element.get("type");
        if (type != null) {
            type = XMaterial.matchXMaterial(type.toString()).orElse(null);
        }
        else if (this.requiresType()) {
            throw new IllegalStateException("material type is required for ConfigItem \"" + element + "\"");
        }

        String name = element.getOrDefault("name", "").toString();
        Object lore = element.getOrDefault("lore", Collections.emptyList());
        if (!(lore instanceof List)) {
            lore = new LinkedList<>(Arrays.asList(lore.toString().split("[\r\n]|<br>")));
        }

        int count = (int) element.getOrDefault("count", -1);
        boolean enchanted = element.getOrDefault("enchanted", "false").toString().equalsIgnoreCase("true");

        Object attributes = element.get("attributes");
        if (!(attributes instanceof Map)) {
            attributes = null;
        }

        if (this.requiresType() || type instanceof XMaterial) {
            return new DefinedConfigItem((XMaterial) type, name, (List<String>) lore, count, enchanted, (Map<String, Object>) attributes);
        }
        else {
            return new VaryingConfigItem(name, (List<String>) lore, count, enchanted, (Map<String, Object>) attributes);
        }
    }
}
