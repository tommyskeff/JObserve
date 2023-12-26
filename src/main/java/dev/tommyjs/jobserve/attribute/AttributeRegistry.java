package dev.tommyjs.jobserve.attribute;

import dev.tommyjs.jobserve.attribute.impl.AttributeRegistryImpl;
import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.key.DuplexKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface AttributeRegistry extends Observable {

    DuplexKey<AttributeKey, Object> UPDATE_ATTRIBUTE_OBSERVER = DuplexKey.register(AttributeKey.class, Object.class);

    <T> @Nullable T getAttribute(@NotNull AttributeKey<T> key);

    <T> @Nullable T getAttributeOrDefault(@NotNull AttributeKey<T> key, @Nullable T defaultValue);

    <T> @NotNull T getAttributeOrCreateDefault(@NotNull AttributeKey<T> key, @NotNull Supplier<@NotNull T> supplier);

    <T> void setAttribute(@NotNull AttributeKey<T> key, @Nullable T value);

    <T> @Nullable T getAttributeOrSetDefault(@NotNull AttributeKey<T> key, @Nullable T defaultValue);

    <T> @Nullable T getAttributeOrSetDefault(@NotNull AttributeKey<T> key, @NotNull Supplier<T> supplier);

    <T> T getAndUpdate(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function);

    <T> Optional<T> getAsOptional(@NotNull AttributeKey<T> key);

    static AttributeRegistry create() {
        return new AttributeRegistryImpl();
    }

}
