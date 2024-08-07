package dev.tommyjs.jobserve.observer.key;

import org.jetbrains.annotations.NotNull;

/**
 * Observer key with a single argument.
 */
@SuppressWarnings({"unused", "unchecked"})
public final class MonoKey<T> extends ObserverKey {

    private final @NotNull Class<? extends T> type;

    private MonoKey(@NotNull Class<? extends T> type) {
        this.type = type;
    }

    public @NotNull Class<? extends T> getType() {
        return type;
    }

    public static <T> @NotNull MonoKey<T> register(@NotNull Class<?> type) {
        return (MonoKey<T>) new MonoKey<>(type);
    }

}
