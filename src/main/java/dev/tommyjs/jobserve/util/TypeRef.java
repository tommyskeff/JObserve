package dev.tommyjs.jobserve.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;

public class TypeRef<T> {

    private final @NotNull Type type;
    private final @NotNull Class<?> raw;

    protected TypeRef() {
        if (getClass().getGenericSuperclass() instanceof ParameterizedType paramType) {
            this.type = paramType.getActualTypeArguments()[0];
            this.raw = resolveClass(type);
        } else {
            throw new IllegalArgumentException(getClass() + " is not parameterized");
        }
    }

    public TypeRef(@NotNull Type type) {
        this.type = type;
        this.raw = resolveClass(type);
    }

    @Contract("null -> null; !null -> !null")
    @SuppressWarnings("unchecked")
    public @Nullable T cast(@Nullable Object object) throws ClassCastException {
        return (T) raw.cast(object);
    }

    public boolean isAssignableFrom(@NotNull Class<?> clazz) {
        return raw.isAssignableFrom(clazz);
    }

    public @NotNull Type getType() {
        return type;
    }

    public @NotNull Class<?> getRawType() {
        return raw;
    }

    public static Class<?> resolveClass(@NotNull Type type) {
        switch (type) {
            case Class<?> clazz -> {
                return clazz;
            }
            case ParameterizedType paramType -> {
                return (Class<?>) paramType.getRawType();
            }
            case GenericArrayType genericArray -> {
                Type componentType = genericArray.getGenericComponentType();
                return Array.newInstance(resolveClass(componentType), 0).getClass();
            }
            case TypeVariable<?> ignored -> {
                return Object.class;
            }
            case WildcardType wildcard -> {
                return resolveClass(wildcard.getUpperBounds()[0]);
            }
            default -> throw new IllegalArgumentException();
        }
    }

}