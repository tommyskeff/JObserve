package dev.tommyjs.jobserve.attribute;

import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverEmitter;
import dev.tommyjs.jobserve.observer.ObserverSubscription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an object that stores attributes that can be retrieved and updated concurrently.
 * <p>
 * Attributes are indexed by {@link AttributeKey} objects, which can be created with {@link AttributeKey#register(Class)}.
 * These keys are used to get and set objects with the type declared in the attribute type parameter.
 */
public interface AttributeHolder extends Observable {

    /**
     * Retrieves the {@link AttributeRegistry} instance powering this {@link AttributeHolder}.
     * This method should always return the same registry instance.
     * @return attribute registry powering this object
     */
    @NotNull AttributeRegistry getAttributes();

    @Override
    default @NotNull ObserverEmitter getEmitter() {
        return getAttributes().getEmitter();
    }

    /**
     * Retrieves an attribute from a given attribute key.
     * @param key attribute key
     * @return current attribute value, or null if not stored
     */
    default <T> @Nullable T getAttribute(@NotNull AttributeKey<T> key) {
        return getAttributes().getAttribute(key);
    }

    /**
     * Retrieves an attribute from a given attribute key. Will throw if the attribute is not
     * present.
     * @param key attribute key
     * @return current attribute value
     * @throws IllegalStateException if the attribute is not present
     */
    default <T> @NotNull T getAttributeOrThrow(@NotNull AttributeKey<T> key) {
        return getAttributes().getAttributeOrThrow(key);
    }

    /**
     * Retrieves an attribute from a given attribute key, but returns a default value if the
     * attribute is not stored. This method will not set the default value as the stored value
     * if the attribute is not stored.
     * @param key attribute key
     * @param defaultValue default value to return if not stored
     * @return current attribute value, or default value if not stored
     */
    default <T> @NotNull T getAttributeOrDefault(@NotNull AttributeKey<T> key, @NotNull T defaultValue) {
        return getAttributes().getAttributeOrDefault(key, defaultValue);
    }

    /**
     * Retrieves an attribute from a given attribute key, but calls default value if the
     * attribute is not stored. This method will set the default value as the stored value
     * if the attribute is not stored.
     * @param key attribute key
     * @param supplier default value supplier to call and return if not stored
     * @return final attribute value, or default value if not stored
     */
    default <T> @NotNull T getAttributeOrCreateDefault(@NotNull AttributeKey<T> key, @NotNull Supplier<@NotNull T> supplier) {
        return getAttributes().getAttributeOrCreateDefault(key, supplier);
    }

    /**
     * Checks whether a given attribute is stored in this attribute holder. It is worth noting
     * that a {@code null} value is treated as the value not being stored.
     * @param key attribute key
     * @return whether a value is stored
     */
    default boolean hasAttribute(@NotNull AttributeKey<?> key) {
        return getAttributes().hasAttribute(key);
    }

    /**
     * Sets an attribute with a given attribute key to a given value. If the given value is
     * null, the value will no longer be stored.
     * @param key attribute key
     * @param value new stored value
     */
    default <T> void setAttribute(@NotNull AttributeKey<T> key, @Nullable T value) {
        getAttributes().setAttribute(key, value);
    }

    /**
     * Clears an attribute with a given attribute key. This action is equivalent to calling
     * {@link #setAttribute(AttributeKey, Object)} with {@code null}.
     * @param key attribute key
     */
    default <T> void clearAttribute(@NotNull AttributeKey<T> key) {
        setAttribute(key, null);
    }

    /**
     * Retrieves an attribute from a given attribute key, but returns a default value if the
     * attribute is not stored. This method will set the default value as the stored value
     * if the attribute is not stored.
     * @param key attribute key
     * @param defaultValue default value to return if not stored
     * @return final attribute value, or default value if not stored
     */
    default <T> @Nullable T getAttributeOrSetDefault(@NotNull AttributeKey<T> key, @NotNull T defaultValue) {
        return getAttributes().getAttributeOrSetDefault(key, defaultValue);
    }

    /**
     * Gets an attribute from a given attribute key, and applies a function to it before storing
     * the value returned by the function.
     * @param key attribute key
     * @param function function to apply to the previous value to obtain the new stored value
     * @return previous stored value
     */
    default <T> @Nullable T getAttributeAndUpdate(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        return getAttributes().getAndUpdate(key, function);
    }

    /**
     * Gets an attribute from a given attribute key, and applies a function to it before storing
     * the value returned by the function.
     * @param key attribute key
     * @param function function to apply to the previous value to obtain the new stored value
     * @return final stored value
     */
    default <T> @Nullable T updateAttributeAndGet(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        return getAttributes().updateAndGet(key, function);
    }

    /**
     * Gets an attribute from a given attribute key as an optional. The optional will be empty
     * if the value is not stored.
     * @param key attribute key
     * @return optional with the stored value
     */
    default <T> @NotNull Optional<T> getAttributeAsOptional(@NotNull AttributeKey<T> key) {
        return getAttributes().getAsOptional(key);
    }

    /**
     * Subscribes to mutations of a given {@link AttributeKey} on an {@link Observable} with attributes.
     * @param key attribute key
     * @param consumer callback
     * @return cancellable subscription
     */
    @SuppressWarnings("unchecked")
    default @NotNull <T> ObserverSubscription observe(@NotNull AttributeKey<T> key, @NotNull Consumer<T> consumer) {
        return observe(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, update -> {
            if (update.key() == key) {
                consumer.accept((T) update.object());
            }
        });
    }

}
