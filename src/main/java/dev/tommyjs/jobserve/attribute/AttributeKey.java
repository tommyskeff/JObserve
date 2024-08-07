package dev.tommyjs.jobserve.attribute;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "unchecked"})
public class AttributeKey<T> {

    private final @NotNull Class<? extends T> clazz;

    protected AttributeKey(@NotNull Class<? extends T> clazz) {
        this.clazz = clazz;
    }

    public @NotNull Class<?> getType() {
        return clazz;
    }

    @Deprecated(since = "0.3.4")
    public @NotNull Class<?> getClazz() {
        return clazz;
    }

    public static <T> @NotNull AttributeKey<T> register(@NotNull Class<?> clazz) {
        return (AttributeKey<T>) new AttributeKey<>(clazz);
    }

}
