package dev.tommyjs.jobserve.observer.key;

import dev.tommyjs.jobserve.util.TypeRef;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a key for an observation. This key may be used for registering subscriptions and for
 * emitting events.
 * @param <T> emitted event type
 */
public final class ObserverKey<T> {

    private final @NotNull TypeRef<? extends T> type;

    private ObserverKey(@NotNull TypeRef<? extends T> type) {
        this.type = type;
    }

    public @NotNull TypeRef<? extends T> getType() {
        return type;
    }

    public static <T> @NotNull ObserverKey<T> register(@NotNull Class<? extends T> type) {
        return new ObserverKey<>(new TypeRef<>(type));
    }

    public static <T> @NotNull ObserverKey<T> register(@NotNull TypeRef<? extends T> type) {
        return new ObserverKey<>(type);
    }

}
