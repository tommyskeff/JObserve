package dev.tommyjs.jobserve.property;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class PropertyKey<T> {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

    private final @NotNull String identifier;
    private final @NotNull TypeToken<? extends T> type;

    private PropertyKey(@NotNull String identifier, @NotNull TypeToken<? extends T> type) {
        this.identifier = identifier;
        this.type = type;
    }

    public @NotNull String getIdentifier() {
        return identifier;
    }

    public @NotNull TypeToken<? extends T> getType() {
        return type;
    }

    public static <T> @NotNull PropertyKey<T> register(@NotNull String identifier, @NotNull Class<? extends T> type) {
        return new PropertyKey<>(identifier, TypeToken.of(type));
    }

    public static <T> @NotNull PropertyKey<T> register(@NotNull String identifier, @NotNull TypeToken<? extends T> type) {
        return new PropertyKey<>(identifier, type);
    }

    public static <T> @NotNull PropertyKey<T> register(@NotNull Class<? extends T> type) {
        return register(String.valueOf(ID_COUNTER.getAndIncrement()), type);
    }

    public static <T> @NotNull PropertyKey<T> register(@NotNull TypeToken<? extends T> type) {
        return register(String.valueOf(ID_COUNTER.getAndIncrement()), type);
    }

}
