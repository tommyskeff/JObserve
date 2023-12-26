package dev.tommyjs.jobserve.observer;

import dev.tommyjs.jobserve.observer.impl.ObserverEmitterImpl;
import dev.tommyjs.jobserve.observer.key.DuplexKey;
import dev.tommyjs.jobserve.observer.key.MonoKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ObserverEmitter {

    <T> @NotNull ObserverSubscription observe(@NotNull MonoKey<T> key, @NotNull Consumer<T> consumer);

    <K, V> @NotNull ObserverSubscription observe(@NotNull DuplexKey<K, V> key, @NotNull BiConsumer<K, V> consumer);

    <T> void emit(@NotNull MonoKey<T> key, @Nullable T value);

    <K, V> void emit(@NotNull DuplexKey<K, V> key, @Nullable K value1, @Nullable V value2);

    void cancel(@NotNull ObserverSubscription subscription);

    static @NotNull ObserverEmitter create() {
        return new ObserverEmitterImpl();
    }

}
