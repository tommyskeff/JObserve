package dev.tommyjs.jobserve.attribute;

import java.security.SecureRandom;
import java.util.Random;

public class AttributeKey<T> {

    private static final Object LOCK = new Object();
    private static final Random RANDOM = new SecureRandom();

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
        synchronized (LOCK) {
            return new AttributeKey<>(RANDOM.nextInt(), clazz);
        }
    }

}
