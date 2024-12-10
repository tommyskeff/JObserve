package dev.tommyjs.jobserve.attribute;

import dev.tommyjs.jobserve.util.TypeRef;
import org.jetbrains.annotations.NotNull;

public class AttributeKey<T> {

    private final @NotNull TypeRef<? extends T> type;

    private AttributeKey(@NotNull TypeRef<? extends T> type) {
        this.type = type;
    }

    public @NotNull TypeRef<? extends T> getType() {
        return type;
    }

    public static <T> @NotNull AttributeKey<T> register(@NotNull Class<? extends T> type) {
        return new AttributeKey<>(new TypeRef<>(type));
    }

    public static <T> @NotNull AttributeKey<T> register(@NotNull TypeRef<? extends T> type) {
        return new AttributeKey<>(type);
    }

}
