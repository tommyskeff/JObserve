package dev.tommyjs.jobserve.observer;

import dev.tommyjs.jobserve.observer.key.DuplexKey;
import dev.tommyjs.jobserve.observer.key.MonoKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Observable {

    @NotNull ObserverEmitter getObserver();

    default <T> @NotNull ObserverSubscription observe(@NotNull MonoKey<T> key, @NotNull Consumer<T> consumer) {
        return getObserver().observe(key, consumer);
    }

    default <K, V> @NotNull ObserverSubscription observe(@NotNull DuplexKey<K, V> key, @NotNull BiConsumer<K, V> consumer) {
        return getObserver().observe(key, consumer);
    }

    default <T> void emit(@NotNull MonoKey<T> key, @Nullable T value) {
        getObserver().emit(key, value);
    }

    default <K, V> void emit(@NotNull DuplexKey<K, V> key, @Nullable K value1, @Nullable V value2) {
        getObserver().emit(key, value1, value2);
    }

}
