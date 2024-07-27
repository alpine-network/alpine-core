package co.crystaldev.alpinecore.util;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.collect.ImmutableSet;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A helper class offering a flexible way to group and test against various material types.
 *
 * @author BestBearr
 * @since 0.3.1
 */
@RequiredArgsConstructor @Getter @ToString
public final class MappedMaterial {

    private final Set<XMaterial> materials;

    /**
     * Tests if the specified {@link XMaterial} is contained within this material map.
     *
     * @param type the material to test
     * @return {@code true} if the material map contains the specified material; {@code false} otherwise.
     */
    public boolean test(@NotNull XMaterial type) {
        return this.materials.contains(type);
    }

    /**
     * Tests if the specified {@link Material} is contained within this material map.
     *
     * @param type the material to test
     * @return {@code true} if the material map contains the specified material; {@code false} otherwise.
     */
    public boolean test(@NotNull Material type) {
        return this.test(MaterialHelper.getType(type));
    }

    /**
     * Tests if the specified {@link Block} is contained within this material map.
     *
     * @param block the block to test
     * @return {@code true} if the material map contains the specified material; {@code false} otherwise.
     */
    public boolean test(@NotNull Block block) {
        return this.test(MaterialHelper.getType(block));
    }

    /**
     * Tests if the specified {@link ItemStack} is contained within this material map.
     *
     * @param item the item to test
     * @return {@code true} if the material map contains the specified material; {@code false} otherwise.
     */
    public boolean test(@NotNull ItemStack item) {
        return this.test(MaterialHelper.getType(item));
    }

    public static @NotNull MappedMaterial of(@NotNull XMaterial... materials) {
        return new MappedMaterial(ImmutableSet.copyOf(materials));
    }

    public static @NotNull MappedMaterial of(@NotNull Collection<XMaterial> materials) {
        return new MappedMaterial(ImmutableSet.copyOf(materials));
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder {

        private final Set<XMaterial> materials = new HashSet<>();

        public @NotNull Builder add(@NotNull MappedMaterial material) {
            this.materials.addAll(material.materials);
            return this;
        }

        public @NotNull Builder add(@NotNull Collection<?> materials) {
            for (Object material : materials) {
                if (material instanceof XMaterial) {
                    this.materials.add((XMaterial) material);
                }
                else if (material instanceof Material) {
                    this.materials.add(XMaterial.matchXMaterial((Material) material));
                }
            }
            return this;
        }

        public @NotNull Builder add(@NotNull Object... materials) {
            for (Object material : materials) {
                if (material instanceof XMaterial) {
                    this.materials.add((XMaterial) material);
                }
                else if (material instanceof Material) {
                    this.materials.add(XMaterial.matchXMaterial((Material) material));
                }
            }
            return this;
        }

        public @NotNull Builder add(@NotNull XMaterial material) {
            this.materials.add(material);
            return this;
        }

        public @NotNull Builder add(@NotNull Material material) {
            this.materials.add(XMaterial.matchXMaterial(material));
            return this;
        }

        public @NotNull Builder add(@NotNull Function<Stream<XMaterial>, Stream<XMaterial>> streamFunction) {
            streamFunction.apply(Stream.of(XMaterial.VALUES).filter(XMaterial::isSupported)).forEach(this::add);
            return this;
        }

        public @NotNull MappedMaterial build() {
            return new MappedMaterial(ImmutableSet.copyOf(this.materials));
        }
    }
}
