package dev.tommyjs.jobserve.attribute;

import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverEmitter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface AttributeHolder extends Observable, AttributeObservable {

    AttributeRegistry getAttributes();

    @Override
    default @NotNull ObserverEmitter getObserver() {
        return getAttributes().getObserver();
    }

    default boolean hasAttribute(@NotNull AttributeKey<?> key) {
        return getAttribute(key) != null;
    }

    default <T> @Nullable T getAttribute(@NotNull AttributeKey<T> key) {
        return getAttributes().getAttribute(key);
    }

    default <T> @Nullable T getAttributeOrDefault(@NotNull AttributeKey<T> key, @Nullable T defaultValue) {
        return getAttributes().getAttributeOrDefault(key, defaultValue);
    }

    default <T> @NotNull T getAttributeOrCreateDefault(@NotNull AttributeKey<T> key, @NotNull Supplier<@NotNull T> supplier) {
        return getAttributes().getAttributeOrCreateDefault(key, supplier);
    }

    default <T> void setAttribute(@NotNull AttributeKey<T> key, @Nullable T value) {
        getAttributes().setAttribute(key, value);
    }

    default <T> @Nullable T getAttributeOrSetDefault(@NotNull AttributeKey<T> key, @Nullable T defaultValue) {
        return getAttributes().getAttributeOrSetDefault(key, defaultValue);
    }

    default <T> @Nullable T getAttributeOrSetDefault(@NotNull AttributeKey<T> key, @NotNull Supplier<T> supplier) {
        return getAttributes().getAttributeOrSetDefault(key, supplier);
    }

    default <T> T getAttributeAndUpdate(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        return getAttributes().getAndUpdate(key, function);
    }

    @Deprecated(forRemoval = true, since = "0.2.0")
    default <T> T getAndUpdateAttribute(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        return getAttributes().getAndUpdate(key, function);
    }

    default <T> Optional<T> getAttibuteAsOptional(@NotNull AttributeKey<T> key) {
        return getAttributes().getAsOptional(key);
    }

    @Deprecated(forRemoval = true, since = "0.2.0")
    default <T> Optional<T> getAsOptional(@NotNull AttributeKey<T> key) {
        return getAttributes().getAsOptional(key);
    }

}
