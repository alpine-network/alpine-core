package co.crystaldev.alpinecore.framework.storage;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Represents a strategy to be used by cache
 * to determine sizing and eviction rules.
 *
 * @see com.google.common.cache.CacheBuilder
 * @author Thomas Wearmouth
 * @since 0.1.0
 */
@Getter
public final class CachingStrategy {
    private final long maximumSize;
    private final long expireTimeValue;
    private final TimeUnit expireTimeUnit;
    private final int concurrencyLevel;

    private CachingStrategy(long maximumSize, long expireTimeValue, @NotNull TimeUnit expireTimeUnit, int concurrencyLevel) {
        this.maximumSize = maximumSize;
        this.expireTimeValue = expireTimeValue;
        this.expireTimeUnit = expireTimeUnit;
        this.concurrencyLevel = concurrencyLevel;
    }

    /**
     * Helper method to return a new builder instance.
     *
     * @see CachingStrategy.Builder
     * @return New builder for this class
     */
    public static @NotNull Builder builder() {
        return new Builder();
    }

    /**
     * Used to construct a new {@link CachingStrategy}.
     *
     * @see co.crystaldev.alpinecore.framework.storage.AlpineStore
     */
    public static final class Builder {
        private long maximumSize = 100;
        private long expireTimeValue = 30;
        private TimeUnit expireTimeUnit = TimeUnit.MINUTES;
        private int concurrencyLevel = 1;

        /**
         * @see com.google.common.cache.CacheBuilder#maximumSize(long)
         */
        @Contract("_ -> this")
        public @NotNull Builder maximumSize(long maximumSize) {
            Validate.isTrue(maximumSize >= -1);
            this.maximumSize = maximumSize;
            return this;
        }

        /**
         * @see com.google.common.cache.CacheBuilder#expireAfterAccess(long, TimeUnit)
         */
        @Contract("_, _ -> this")
        public @NotNull Builder expireTime(long expireTimeValue, TimeUnit expireTimeUnit) {
            return this.expireTimeValue(expireTimeValue).expireTimeUnit(expireTimeUnit);
        }

        /**
         * @see com.google.common.cache.CacheBuilder#expireAfterAccess(long, TimeUnit) 
         */
        @Contract("_ -> this")
        public @NotNull Builder expireTimeValue(long expireTimeValue) {
            Validate.isTrue(this.expireTimeValue > 0);
            this.expireTimeValue = expireTimeValue;
            return this;
        }

        /**
         * @see com.google.common.cache.CacheBuilder#expireAfterAccess(long, TimeUnit)
         */
        @Contract("null -> fail; _ -> this")
        public @NotNull Builder expireTimeUnit(TimeUnit expireTimeUnit) {
            Validate.notNull(this.expireTimeUnit);
            this.expireTimeUnit = expireTimeUnit;
            return this;
        }

        /**
         * @see com.google.common.cache.CacheBuilder#concurrencyLevel(int) 
         */
        @Contract("_ -> this")
        public @NotNull Builder concurrencyLevel(int concurrencyLevel) {
            Validate.isTrue(this.concurrencyLevel > 0);
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }

        /**
         * @return The newly constructed {@link CachingStrategy}
         */
        public @NotNull CachingStrategy build() {
            return new CachingStrategy(this.maximumSize, this.expireTimeValue, this.expireTimeUnit, this.concurrencyLevel);
        }
    }
}
