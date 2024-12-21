package dev.tommyjs.jobserve.property;

import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverEmitter;
import dev.tommyjs.jobserve.observer.ObserverSub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents an object that stores properties that can be retrieved and updated concurrently.
 * <p>
 * Properties are indexed by {@link PropertyKey} objects, which can be created with {@link PropertyKey#register(Class)}.
 * These keys are used to get and set objects with the type declared in the property type parameter.
 */
public interface PropertyHolder extends Observable {

    /**
     * Retrieves the {@link PropertyMap} instance powering this {@link PropertyHolder}.
     * This method should always return the same registry instance.
     * @return property registry powering this object
     */
    @NotNull PropertyMap getPropertyMap();

    @Override
    default @NotNull ObserverEmitter getEmitter() {
        return getPropertyMap().getEmitter();
    }

    /**
     * Retrieves an property from a given property key.
     * @param key property key
     * @return current property value, or null if not stored
     */
    default <T> @Nullable T getProperty(@NotNull PropertyKey<T> key) {
        return getPropertyMap().getProperty(key);
    }

    /**
     * Retrieves a property value based on a given string key.
     * @param key the string key identifying the property
     * @return the current property value as an Object, or null if not stored
     */
    default @Nullable Object getProperty(@NotNull String key) {
        return getPropertyMap().getProperty(key);
    }

    /**
     * Retrieves an property from a given property key. Will throw if the property is not
     * present.
     * @param key property key
     * @return current property value
     * @throws IllegalStateException if the property is not present
     */
    default <T> @NotNull T getPropertyOrThrow(@NotNull PropertyKey<T> key) {
        return getPropertyMap().getPropertyOrThrow(key);
    }

    /**
     * Retrieves a property value based on a given string key. Will throw if the property is not present.
     * @param key the string key identifying the property
     * @return the current property value as an Object
     * @throws IllegalStateException if the property is not present
     */
    default @NotNull Object getPropertyOrThrow(@NotNull String key) {
        return getPropertyMap().getPropertyOrThrow(key);
    }

    /**
     * Retrieves an property from a given property key, but returns a default value if the
     * property is not stored. This method will not set the default value as the stored value
     * if the property is not stored.
     * @param key property key
     * @param defaultValue default value to return if not stored
     * @return current property value, or default value if not stored
     */
    default <T> @NotNull T getPropertyOrDefault(@NotNull PropertyKey<T> key, @NotNull T defaultValue) {
        return getPropertyMap().getPropertyOrDefault(key, defaultValue);
    }

    /**
     * Retrieves a property value based on a given string key, but returns a default value if the
     * property is not stored. This method will not set the default value as the stored value
     * if the property is not stored.
     * @param key the string key identifying the property
     * @param defaultValue the default value to return if the property is not stored
     * @return the current property value as an Object, or the default value if not stored
     */
    default @NotNull Object getPropertyOrDefault(@NotNull String key, @NotNull Object defaultValue) {
        return getPropertyMap().getPropertyOrDefault(key, defaultValue);
    }

    /**
     * Retrieves an property from a given property key, but calls default value if the
     * property is not stored. This method will set the default value as the stored value
     * if the property is not stored.
     * @param key property key
     * @param supplier default value supplier to call and return if not stored
     * @return final property value, or default value if not stored
     */
    default <T> @NotNull T getPropertyOrCreateDefault(@NotNull PropertyKey<T> key, @NotNull Supplier<@NotNull T> supplier) {
        return getPropertyMap().getPropertyOrCreateDefault(key, supplier);
    }

    /**
     * Retrieves a property value based on a given string key, but calls the default value supplier if the
     * property is not stored. This method will set the default value as the stored value
     * if the property is not stored.
     * @param key the string key identifying the property
     * @param supplier the default value supplier to call and return if the property is not stored
     * @return the final property value as an Object, or the default value if not stored
     */
    default @NotNull Object getPropertyOrCreateDefault(@NotNull String key, @NotNull Supplier<@NotNull Object> supplier) {
        return getPropertyMap().getPropertyOrCreateDefault(key, supplier);
    }

    /**
     * Checks whether a given property is stored in this property holder. It is worth noting
     * that a null value is treated as the value not being stored.
     * @param key property key
     * @return whether a value is stored
     */
    default boolean hasProperty(@NotNull PropertyKey<?> key) {
        return getPropertyMap().hasProperty(key);
    }

    /**
     * Checks whether a given property is stored in this property holder. It is worth noting
     * that a null value is treated as the value not being stored.
     * @param key the string key identifying the property
     * @return whether a value is stored
     */
    default boolean hasProperty(@NotNull String key) {
        return getPropertyMap().hasProperty(key);
    }

    /**
     * Sets an property with a given property key to a given value. If the given value is
     * null, the value will no longer be stored.
     * @param key property key
     * @param value new stored value
     */
    default <T> void setProperty(@NotNull PropertyKey<T> key, @Nullable T value) {
        getPropertyMap().setProperty(key, value);
    }

    /**
     * Sets a property with a given string key to a given value. If the given value is
     * null, the value will no longer be stored.
     * @param key the string key identifying the property
     * @param value the new stored value, or null to remove the property
     */
    default void setProperty(@NotNull String key, @Nullable Object value) {
        getPropertyMap().setProperty(key, value);
    }

    /**
     * Clears an property with a given property key. This action is equivalent to calling
     * {@link #setProperty(PropertyKey, Object)} with {@code null}.
     * @param key property key
     */
    default <T> void clearProperty(@NotNull PropertyKey<T> key) {
        getPropertyMap().clearProperty(key);
    }

    /**
     * Clears a property with a given string key. This action is equivalent to calling
     * {@link #setProperty(String, Object)} with {@code null}.
     * @param key the string key identifying the property
     */
    default void clearProperty(@NotNull String key) {
        getPropertyMap().clearProperty(key);
    }

    /**
     * Retrieves an property from a given property key, but returns a default value if the
     * property is not stored. This method will set the default value as the stored value
     * if the property is not stored.
     * @param key property key
     * @param defaultValue default value to return if not stored
     * @return final property value, or default value if not stored
     */
    default <T> @NotNull T getPropertyOrSetDefault(@NotNull PropertyKey<T> key, @NotNull T defaultValue) {
        return getPropertyMap().getPropertyOrSetDefault(key, defaultValue);
    }

    /**
     * Retrieves a property value based on a given string key, but returns a default value if the
     * property is not stored. This method will set the default value as the stored value
     * if the property is not stored.
     * @param key the string key identifying the property
     * @param defaultValue the default value to return and set if the property is not stored
     * @return the final property value as an Object, or the default value if not stored
     */
    default @NotNull Object getPropertyOrSetDefault(@NotNull String key, @NotNull Object defaultValue) {
        return getPropertyMap().getPropertyOrSetDefault(key, defaultValue);
    }

    /**
     * Gets an property from a given property key, and applies a function to it before storing
     * the value returned by the function.
     * @param key property key
     * @param function function to apply to the previous value to obtain the new stored value
     * @return previous stored value
     */
    default <T> @Nullable T getPropertyAndUpdate(@NotNull PropertyKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        return getPropertyMap().getPropertyAndUpdate(key, function);
    }

    /**
     * Gets a property based on a given string key, and applies a function to it before storing
     * the value returned by the function.
     * @param key the string key identifying the property
     * @param function the function to apply to the previous value to obtain the new stored value
     * @return the previous stored value as an Object
     */
    default @Nullable Object getPropertyAndUpdate(@NotNull String key, @NotNull Function<@Nullable Object, @Nullable Object> function) {
        return getPropertyMap().getPropertyAndUpdate(key, function);
    }

    /**
     * Gets an property from a given property key, and applies a function to it before storing
     * the value returned by the function.
     * @param key property key
     * @param function function to apply to the previous value to obtain the new stored value
     * @return final stored value
     */
    default <T> @Nullable T updatePropertyAndGet(@NotNull PropertyKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        return getPropertyMap().updatePropertyAndGet(key, function);
    }

    /**
     * Gets a property based on a given string key, and applies a function to it before storing
     * the value returned by the function.
     * @param key the string key identifying the property
     * @param function the function to apply to the previous value to obtain the new stored value
     * @return the final stored value as an Object
     */
    default @Nullable Object updatePropertyAndGet(@NotNull String key, @NotNull Function<@Nullable Object, @Nullable Object> function) {
        return getPropertyMap().updatePropertyAndGet(key, function);
    }

    /**
     * Gets an property from a given property key as an optional. The optional will be empty
     * if the value is not stored.
     * @param key property key
     * @return optional with the stored value
     */
    default <T> @NotNull Optional<T> getPropertyAsOptional(@NotNull PropertyKey<T> key) {
        return getPropertyMap().getPropertyAsOptional(key);
    }

    /**
     * Gets a property based on a given string key as an optional. The optional will be empty
     * if the value is not stored.
     * @param key the string key identifying the property
     * @return an optional with the stored value
     */
    default @NotNull Optional<Object> getPropertyAsOptional(@NotNull String key) {
        return getPropertyMap().getPropertyAsOptional(key);
    }

    /**
     * Gets all property entries in this property map.
     * @return property entries
     */
    default @NotNull Collection<PropertyEntry> getProperties() {
        return getPropertyMap().getProperties();
    }

    /**
     * Clones all properties from this holder into another target holder.
     * @param target target property holder
     */
    default void copyPropertiesInto(@NotNull PropertyHolder target) {
        getPropertyMap().copyInto(target.getPropertyMap());
    }

    /**
     * Clones all properties from a target holder into this holder.
     * @param target target property holder
     */
    default void copyPropertiesFrom(@NotNull PropertyHolder target) {
        target.getPropertyMap().copyInto(getPropertyMap());
    }

    /**
     * Clears all properties from this property map.
     */
    default void clearProperties() {
        getPropertyMap().clear();
    }

    /**
     * Subscribes to mutations of all properties.
     * @param consumer callback
     * @return observer subscription
     */
    default @NotNull ObserverSub observe(@NotNull Consumer<PropertyUpdate> consumer) {
        return observe(PropertyMap.UPDATE_PROPERTY_KEY, consumer);
    }

    /**
     * Subscribes to mutations of a given {@link PropertyKey} on an {@link Observable} with properties.
     * @param key property key
     * @param consumer callback
     * @return observer subscription
     */
    default @NotNull ObserverSub observe(@NotNull String key, @NotNull BiConsumer<Object, Object> consumer) {
        return observe(PropertyMap.UPDATE_PROPERTY_KEY, update -> {
            if (update.key().equals(key)) {
                consumer.accept(update.prev(), update.curr());
            }
        });
    }

    /**
     * Subscribes to mutations of a given {@link PropertyKey} on an {@link Observable} with properties.
     * @param key property key
     * @param consumer callback
     * @return observer subscription
     */
    @SuppressWarnings("unchecked")
    default @NotNull <T> ObserverSub observe(@NotNull PropertyKey<T> key, @NotNull BiConsumer<T, T> consumer) {
        return observe(key.getIdentifier(), (BiConsumer<Object, Object>) consumer);
    }

}
