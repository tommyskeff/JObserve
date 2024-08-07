package dev.tommyjs.jobserve.observer.key;

import org.jetbrains.annotations.NotNull;

/**
 * Observer key with two arguments.
 */
@SuppressWarnings({"unused", "unchecked"})
public final class DuplexKey<K, V> extends ObserverKey {

    private final @NotNull Class<K> type1;
    private final @NotNull Class<V> type2;

    private DuplexKey(@NotNull Class<K> type1, @NotNull Class<V> type2) {
        this.type1 = type1;
        this.type2 = type2;
    }

    public @NotNull Class<K> getType1() {
        return type1;
    }

    public @NotNull Class<V> getType2() {
        return type2;
    }

    public static <K, V> DuplexKey<K, V> register(@NotNull Class<?> type1, @NotNull Class<?> type2) {
        return (DuplexKey<K, V>) new DuplexKey<>(type1, type2);
    }

}
