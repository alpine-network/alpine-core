package co.crystaldev.alpinecore.framework;

/**
 * Defines an interface for objects that require late initialization.
 *
 * @author BestBearr
 * @since 0.2.0
 */
public interface Initializable {

    /**
     * Initializes the object. Implementations should perform all necessary setup here.
     *
     * @return boolean Returns {@code true} if initialization is successful, {@code false} otherwise.
     */
    boolean init();
}
