package dev.tommyjs.jobserve.property;

import com.google.common.reflect.TypeToken;
import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverEmitter;
import dev.tommyjs.jobserve.observer.key.ObserverKey;
import dev.tommyjs.jobserve.property.impl.PropertyMapImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Property maps are concurrent indexes of property keys to values. These values can be
 * retrieved and mutated.
 */
public interface PropertyMap extends Observable {

    ObserverKey<PropertyUpdate> UPDATE_PROPERTY_KEY = ObserverKey.register(new TypeToken<>(){});

    /**
     * Retrieves an property from a given property key.
     * @param key property key
     * @return current property value, or null if not stored
     */
    <T> @Nullable T getProperty(@NotNull PropertyKey<T> key);

    /**
     * Retrieves a property value based on a given string key.
     * @param key the string key identifying the property
     * @return the current property value as an Object, or null if not stored
     */
    @Nullable Object getProperty(@NotNull String key);

    /**
     * Retrieves an property from a given property key. Will throw if the property is not
     * present.
     * @param key property key
     * @return current property value
     * @throws IllegalStateException if the property is not present
     */
    <T> @NotNull T getPropertyOrThrow(@NotNull PropertyKey<T> key);

    /**
     * Retrieves a property value based on a given string key. Will throw if the property is not present.
     * @param key the string key identifying the property
     * @return the current property value as an Object
     * @throws IllegalStateException if the property is not present
     */
    @NotNull Object getPropertyOrThrow(@NotNull String key);

    /**
     * Retrieves an property from a given property key, but returns a default value if the
     * property is not stored. This method will not set the default value as the stored value
     * if the property is not stored.
     * @param key property key
     * @param defaultValue default value to return if not stored
     * @return current property value, or default value if not stored
     */
    <T> @NotNull T getPropertyOrDefault(@NotNull PropertyKey<T> key, @NotNull T defaultValue);

    /**
     * Retrieves a property value based on a given string key, but returns a default value if the
     * property is not stored. This method will not set the default value as the stored value
     * if the property is not stored.
     * @param key the string key identifying the property
     * @param defaultValue the default value to return if the property is not stored
     * @return the current property value as an Object, or the default value if not stored
     */
    @NotNull Object getPropertyOrDefault(@NotNull String key, @NotNull Object defaultValue);


    /**
     * Retrieves an property from a given property key, but calls default value if the
     * property is not stored. This method will set the default value as the stored value
     * if the property is not stored.
     * @param key property key
     * @param supplier default value supplier to call and return if not stored
     * @return final property value, or default value if not stored
     */
    <T> @NotNull T getPropertyOrCreateDefault(@NotNull PropertyKey<T> key, @NotNull Supplier<@NotNull T> supplier);

    /**
     * Retrieves a property value based on a given string key, but calls the default value supplier if the
     * property is not stored. This method will set the default value as the stored value
     * if the property is not stored.
     * @param key the string key identifying the property
     * @param supplier the default value supplier to call and return if the property is not stored
     * @return the final property value as an Object, or the default value if not stored
     */
    @NotNull Object getPropertyOrCreateDefault(@NotNull String key, @NotNull Supplier<@NotNull Object> supplier);

    /**
     * Checks whether a given property is stored in this property holder. It is worth noting
     * that a null value is treated as the value not being stored.
     * @param key property key
     * @return whether a value is stored
     */
    default boolean hasProperty(@NotNull PropertyKey<?> key) {
        return getProperty(key) != null;
    }

    /**
     * Checks whether a given property is stored in this property holder. It is worth noting
     * that a null value is treated as the value not being stored.
     * @param key the string key identifying the property
     * @return whether a value is stored
     */
    default boolean hasProperty(@NotNull String key) {
        return getProperty(key) != null;
    }

    /**
     * Sets an property with a given property key to a given value. If the given value is
     * null, the value will no longer be stored.
     * @param key property key
     * @param value new stored value
     */
    <T> void setProperty(@NotNull PropertyKey<T> key, @Nullable T value);

    /**
     * Sets a property with a given string key to a given value. If the given value is
     * null, the value will no longer be stored.
     * @param key the string key identifying the property
     * @param value the new stored value, or null to remove the property
     */
    void setProperty(@NotNull String key, @Nullable Object value);

    /**
     * Clears an property with a given property key. This action is equivalent to calling
     * {@link #setProperty(PropertyKey, Object)} with {@code null}.
     * @param key property key
     */
    default <T> void clearProperty(@NotNull PropertyKey<T> key) {
        setProperty(key, null);
    }

    /**
     * Clears a property with a given string key. This action is equivalent to calling
     * {@link #setProperty(String, Object)} with {@code null}.
     * @param key the string key identifying the property
     */
    default void clearProperty(@NotNull String key) {
        setProperty(key, null);
    }

    /**
     * Retrieves an property from a given property key, but returns a default value if the
     * property is not stored. This method will set the default value as the stored value
     * if the property is not stored.
     * @param key property key
     * @param defaultValue default value to return if not stored
     * @return final property value, or default value if not stored
     */
    <T> @NotNull T getPropertyOrSetDefault(@NotNull PropertyKey<T> key, @NotNull T defaultValue);

    /**
     * Retrieves a property value based on a given string key, but returns a default value if the
     * property is not stored. This method will set the default value as the stored value
     * if the property is not stored.
     * @param key the string key identifying the property
     * @param defaultValue the default value to return and set if the property is not stored
     * @return the final property value as an Object, or the default value if not stored
     */
    @NotNull Object getPropertyOrSetDefault(@NotNull String key, @NotNull Object defaultValue);

    /**
     * Gets an property from a given property key, and applies a function to it before storing
     * the value returned by the function.
     * @param key property key
     * @param function function to apply to the previous value to obtain the new stored value
     * @return previous stored value
     */
    <T> @Nullable T getPropertyAndUpdate(@NotNull PropertyKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function);

    /**
     * Gets a property based on a given string key, and applies a function to it before storing
     * the value returned by the function.
     * @param key the string key identifying the property
     * @param function the function to apply to the previous value to obtain the new stored value
     * @return the previous stored value as an Object
     */
    @Nullable Object getPropertyAndUpdate(@NotNull String key, @NotNull Function<@Nullable Object, @Nullable Object> function);

    /**
     * Gets an property from a given property key, and applies a function to it before storing
     * the value returned by the function.
     * @param key property key
     * @param function function to apply to the previous value to obtain the new stored value
     * @return final stored value
     */
    <T> @Nullable T updatePropertyAndGet(@NotNull PropertyKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function);

    /**
     * Gets a property based on a given string key, and applies a function to it before storing
     * the value returned by the function.
     * @param key the string key identifying the property
     * @param function the function to apply to the previous value to obtain the new stored value
     * @return the final stored value as an Object
     */
    @Nullable Object updatePropertyAndGet(@NotNull String key, @NotNull Function<@Nullable Object, @Nullable Object> function);

    /**
     * Gets an property from a given property key as an optional. The optional will be empty
     * if the value is not stored.
     * @param key property key
     * @return optional with the stored value
     */
    <T> @NotNull Optional<T> getPropertyAsOptional(@NotNull PropertyKey<T> key);

    /**
     * Gets a property based on a given string key as an optional. The optional will be empty
     * if the value is not stored.
     * @param key the string key identifying the property
     * @return an optional with the stored value
     */
    @NotNull Optional<Object> getPropertyAsOptional(@NotNull String key);

    /**
     * Gets all property entries in this property map.
     * @return property entries
     */
    @NotNull Collection<PropertyEntry> getProperties();

    /**
     * Clones all properties from this map into another target map.
     * @param target target property map
     */
    void copyInto(@NotNull PropertyMap target);

    /**
     * Clears all properties from this property map.
     */
    void clear();

    /**
     * Creates a new default {@link PropertyMap} instance.
     * @return new property map
     */
    static @NotNull PropertyMap create() {
        return new PropertyMapImpl();
    }

    /**
     * Creates a new default {@link PropertyMap} instance with a specified emitter.
     * @return new property map
     */
    static @NotNull PropertyMap create(@NotNull ObserverEmitter emitter) {
        return new PropertyMapImpl(emitter);
    }

    record PropertyUpdate(@NotNull String key, @Nullable Object prev, @Nullable Object curr) {
    }

}
