package dev.tommyjs.jobserve.attribute;

import dev.tommyjs.jobserve.attribute.impl.AttributeRegistryImpl;
import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.key.DuplexKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Attribute registries are concurrent indexes of attribute keys to values. These values can be
 * retrieved and mutated.
 */
public interface AttributeRegistry extends Observable {

    DuplexKey<AttributeKey, Object> UPDATE_ATTRIBUTE_OBSERVER = DuplexKey.register(AttributeKey.class, Object.class);

    /**
     * Retrieves an attribute from a given attribute key.
     * @param key attribute key
     * @return current attribute value, or null if not stored
     */
    <T> @Nullable T getAttribute(@NotNull AttributeKey<T> key);

    /**
     * Retrieves an attribute from a given attribute key, but returns a default value if the
     * attribute is not stored. This method will not set the default value as the stored value
     * if the attribute is not stored.
     * @param key attribute key
     * @param defaultValue default value to return if not stored
     * @return current attribute value, or default value if not stored
     */
    <T> @NotNull T getAttributeOrDefault(@NotNull AttributeKey<T> key, @NotNull T defaultValue);

    /**
     * Retrieves an attribute from a given attribute key, but calls default value if the
     * attribute is not stored. This method will set the default value as the stored value
     * if the attribute is not stored.
     * @param key attribute key
     * @param supplier default value supplier to call and return if not stored
     * @return final attribute value, or default value if not stored
     */
    <T> @NotNull T getAttributeOrCreateDefault(@NotNull AttributeKey<T> key, @NotNull Supplier<@NotNull T> supplier);

    /**
     * Sets an attribute with a given attribute key to a given value. If the given value is
     * null, the value will no longer be stored.
     * @param key attribute key
     * @param value new stored value
     */
    <T> void setAttribute(@NotNull AttributeKey<T> key, @Nullable T value);

    /**
     * Retrieves an attribute from a given attribute key, but returns a default value if the
     * attribute is not stored. This method will set the default value as the stored value
     * if the attribute is not stored.
     * @param key attribute key
     * @param defaultValue default value to return if not stored
     * @return final attribute value, or default value if not stored
     */
    <T> @NotNull T getAttributeOrSetDefault(@NotNull AttributeKey<T> key, @NotNull T defaultValue);

    /**
     * Gets an attribute from a given attribute key, and applies a function to it before storing
     * the value returned by the function.
     * @param key attribute key
     * @param function function to apply to the previous value to obtain the new stored value
     * @return previous stored value
     */
    <T> @Nullable T getAndUpdate(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function);

    /**
     * Gets an attribute from a given attribute key, and applies a function to it before storing
     * the value returned by the function.
     * @param key attribute key
     * @param function function to apply to the previous value to obtain the new stored value
     * @return final stored value
     */
    <T> @Nullable T updateAndGet(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function);

    /**
     * Gets an attribute from a given attribute key as an optional. The optional will be empty
     * if the value is not stored.
     * @param key attribute key
     * @return optional with the stored value
     */
    <T> @NotNull Optional<T> getAsOptional(@NotNull AttributeKey<T> key);

    /**
     * Creates a new default {@link AttributeRegistry} instance.
     * @return new attribute registry
     */
    static @NotNull AttributeRegistry create() {
        return new AttributeRegistryImpl();
    }

}
