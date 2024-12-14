package dev.tommyjs.jobserve.observer.key;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a key for an observation. This key may be used for registering subscriptions and for
 * emitting events.
 * @param <T> emitted event type
 */
public final class ObserverKey<T> {

    private final @NotNull TypeToken<? extends T> type;

    private ObserverKey(@NotNull TypeToken<? extends T> type) {
        this.type = type;
    }

    public @NotNull TypeToken<? extends T> getType() {
        return type;
    }

    public static <T> @NotNull ObserverKey<T> register(@NotNull Class<? extends T> type) {
        return new ObserverKey<>(TypeToken.of(type));
    }

    public static <T> @NotNull ObserverKey<T> register(@NotNull TypeToken<? extends T> type) {
        return new ObserverKey<>(type);
    }

}
