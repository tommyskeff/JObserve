package dev.tommyjs.jobserve.observer.key;

import org.jetbrains.annotations.NotNull;

/**
 * Observer key with a single argument.
 */
@SuppressWarnings("unused")
public final class MonoKey<T> extends ObserverKey {

    private final @NotNull Class<T> type;

    private MonoKey(int keyId, @NotNull Class<T> type) {
        super(keyId);
        this.type = type;
    }

    public @NotNull Class<T> getType() {
        return type;
    }

    public static <T> @NotNull MonoKey<T> register(@NotNull Class<T> type) {
        return new MonoKey<>(INDEX.getAndIncrement(), type);
    }

}
