package co.crystaldev.alpinecore.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.UUID;

/**
 * An adapter for utilizing non-dashed UUIDs in Gson.
 *
 * @author BestBearr
 * @since 0.1.0
 */
public final class UuidTypeAdapter extends TypeAdapter<UUID> {
    @Override
    public void write(JsonWriter out, UUID value) throws IOException {
        out.value(fromUUID(value));
    }

    @Override
    public UUID read(JsonReader in) throws IOException {
        return fromString(in.nextString());
    }

    /**
     * Converts a UUID object into a non-dashed string.
     *
     * @param value The UUID object
     * @return The non-dashed string
     */
    public static String fromUUID(UUID value) {
        return value.toString().replace("-", "");
    }

    /**
     * Converts a non-dashed string into a UUID object.
     *
     * @param input The non-dashed string
     * @return The UUID object
     */
    public static UUID fromString(String input) {
        return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
