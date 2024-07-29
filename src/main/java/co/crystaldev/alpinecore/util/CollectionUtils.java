package co.crystaldev.alpinecore.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Utility for instantiating commonly used {@link Collection}s with concise
 * and clean syntax.
 *
 * @author Thomas Wearmouth
 * @since 0.1.1
 */
@UtilityClass @SuppressWarnings("unchecked")
public final class CollectionUtils {
    /**
     * Creates a new list with the specified objects.
     * <p>
     * This method makes no assertion as to the ordering of
     * the list. For an ordered list, use {@link CollectionUtils#linkedList(Object[])}.
     *
     * @param <T> the type of the elements in the list
     *
     * @param items the objects to add to the list
     * @return the list
     *
     * @see java.util.ArrayList
     */
    @SafeVarargs @Contract("null -> fail")
    public static <T> @NotNull List<T> list(@NotNull T... items) {
        ArrayList<T> list = new ArrayList<>(items.length);
        Collections.addAll(list, items);
        return list;
    }

    /**
     * Creates a new ordered list with the specified objects.
     *
     * @param <T> the type of the elements in the list
     *
     * @param items the objects to add to the list
     * @return the list
     *
     * @see java.util.LinkedList
     */
    @SafeVarargs @Contract("null -> fail")
    public static <T> @NotNull LinkedList<T> linkedList(@NotNull T... items) {
        LinkedList<T> list = new LinkedList<>();
        Collections.addAll(list, items);
        return list;
    }

    /**
     * Creates a new set with the specified objects.
     * <p>
     * This method makes no assertion as to the ordering of
     * the list. For an ordered list, use {@link CollectionUtils#linkedSet(Object[])}.
     *
     * @param <T> the type of the elements in the set
     *
     * @param items the objects to add to the set
     * @return the set
     *
     * @see java.util.HashSet
     */
    @SafeVarargs @Contract("null -> fail")
    public static <T> @NotNull Set<T> set(@NotNull T... items) {
        HashSet<T> set = new HashSet<>(items.length);
        Collections.addAll(set, items);
        return set;
    }

    /**
     * Creates a new ordered list with the specified objects.
     *
     * @param <T> the type of the elements in the set
     *
     * @param items the objects to add to the set
     * @return the set
     *
     * @see java.util.LinkedHashSet
     */
    @SafeVarargs @Contract("null -> fail")
    public static <T> @NotNull LinkedHashSet<T> linkedSet(@NotNull T... items) {
        LinkedHashSet<T> set = new LinkedHashSet<>(items.length);
        Collections.addAll(set, items);
        return set;
    }
    
    /**
     * Creates a new map with the specified objects.
     * <p>
     * This method makes no assertion as to the ordering of
     * the map. For an ordered map, use {@link CollectionUtils#linkedMap(Object, Object, Object...)}.
     * <p>
     * Varargs should be specified by alternating keys and values.
     * <p>
     * If the number of varargs is odd, the method will quietly
     * ignore them.
     *
     * @param <K> the type of the keys in the map
     * @param <V> the type of the values in the map
     *
     * @param items the keys and values to add to the map
     * @return the map
     *
     * @see java.util.HashMap
     */
    public static <K, V> @NotNull Map<K, V> map(@NotNull K firstKey, @NotNull V firstValue, @NotNull Object... items) {
        int initialCapacity = (items.length / 2) + 1;
        HashMap<K, V> map = new HashMap<>(initialCapacity);
        map.put(firstKey, firstValue);

        if (items.length > 1 || items.length % 2 != 0) {
            for (int i = 0; i < (items.length / 2) * 2; i += 2) {
                K key = (K) items[i];
                V value = (V) items[i + 1];

                map.put(key, value);
            }
        }

        return map;
    }

    /**
     * Creates a new map with the specified items.
     *
     * @param <T> the type of the elements in the map
     *
     * @param items the items to add to the map
     * @return the map
     *
     * @see HashMap
     */
    public static <T> @NotNull Map<T, T> mapFromArray(@NotNull T... items) {
        HashMap<T, T> map = new HashMap<>(items.length / 2);
        for (int i = 0; i < items.length; i += 2) {
            map.put(items[i], items[i + 1]);
        }
        return map;
    }

    /**
     * Creates a new ordered map with the specified objects.
     * <p>
     * Varargs should be specified by alternating keys and values.
     * <p>
     * If the number of varargs is odd, the method will quietly
     * ignore them.
     *
     * @param <K> the type of the keys in the map
     * @param <V> the type of the values in the map
     *
     * @param items the keys and values to add to the map
     * @return the map
     *
     * @see java.util.LinkedHashMap
     */
    public static <K, V> @NotNull LinkedHashMap<K, V> linkedMap(@NotNull K firstKey, @NotNull V firstValue, @NotNull Object... items) {
        int initialCapacity = (items.length / 2) + 1;
        LinkedHashMap<K, V> map = new LinkedHashMap<>(initialCapacity);
        map.put(firstKey, firstValue);

        if (items.length > 1 || items.length % 2 != 0) {
            for (int i = 0; i < (items.length / 2) * 2; i += 2) {
                K key = (K) items[i];
                V value = (V) items[i + 1];

                map.put(key, value);
            }
        }

        return map;
    }

    /**
     * Creates a new LinkedHashMap with the specified objects.
     * <p>
     * Varargs should be specified by alternating keys and values.
     * <p>
     * If the number of varargs is odd, the method will quietly ignore them.
     *
     * @param <T>    the type of the elements in the map
     * @param items  the keys and values to add to the map
     * @return the map
     *
     * @see java.util.LinkedHashMap
     */
    public static <T> @NotNull LinkedHashMap<T, T> linkedMapFromArray(@NotNull T... items) {
        LinkedHashMap<T, T> map = new LinkedHashMap<>(items.length / 2);
        for (int i = 0; i < items.length; i += 2) {
            map.put(items[i], items[i + 1]);
        }
        return map;
    }
}
