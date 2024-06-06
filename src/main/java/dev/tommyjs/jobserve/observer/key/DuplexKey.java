package dev.tommyjs.jobserve.observer.key;

import org.jetbrains.annotations.NotNull;

/**
 * Observer key with two arguments.
 */
@SuppressWarnings("unused")
public final class DuplexKey<K, V> extends ObserverKey {

    private final @NotNull Class<K> type1;
    private final @NotNull Class<V> type2;

    private DuplexKey(int keyId, @NotNull Class<K> type1, @NotNull Class<V> type2) {
        super(keyId);
        this.type1 = type1;
        this.type2 = type2;
    }

    public @NotNull Class<K> getType1() {
        return type1;
    }

    public @NotNull Class<V> getType2() {
        return type2;
    }

    public static <K, V> DuplexKey<K, V> register(@NotNull Class<K> type1, @NotNull Class<V> type2) {
        return new DuplexKey<>(RANDOM.nextInt(), type1, type2);
    }

}
