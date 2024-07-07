package dev.tommyjs.jobserve.attribute;

import java.util.concurrent.atomic.AtomicInteger;

public class AttributeKey<T> {

    private static final AtomicInteger INDEX = new AtomicInteger();

    private final int id;
    private final Class<?> clazz;

    protected AttributeKey(int id, Class<?> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeKey<?> that)) return false;
        return id == that.id;
    }

    public static <T> AttributeKey<T> register(Class<T> clazz) {
        return new AttributeKey<>(INDEX.getAndIncrement(), clazz);
    }

}
