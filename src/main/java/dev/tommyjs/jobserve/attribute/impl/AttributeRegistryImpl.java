package dev.tommyjs.jobserve.attribute.impl;

import dev.tommyjs.jobserve.attribute.AttributeKey;
import dev.tommyjs.jobserve.attribute.AttributeRegistry;
import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverEmitter;
import dev.tommyjs.jobserve.observer.impl.ObserverEmitterImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class AttributeRegistryImpl implements AttributeRegistry, Observable {

    private final ObserverEmitter emitter;
    private final ReadWriteLock mutex;
    private final Map<AttributeKey<?>, Object> data;

    public AttributeRegistryImpl() {
        this.emitter = new ObserverEmitterImpl();
        this.mutex = new ReentrantReadWriteLock();
        this.data = new ConcurrentHashMap<>();
    }

    protected <T> void checkArgType(@NotNull AttributeKey<T> key, @Nullable T value) {
        if (value != null && !key.getClazz().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Assignment to attribute of type " + key.getClazz().getName() + " with " +
                "value of type " + value.getClass().getName());
        }
    }

    protected <T> void checkValueType(@NotNull AttributeKey<T> key, @Nullable Object value) {
        if (value != null && !key.getClazz().isAssignableFrom(value.getClass())) {
            throw new IllegalStateException("Attribute of type " + key.getClazz().getName() + " contains value " +
                "of type " + value.getClass().getName());
        }
    }

    @Override
    public <T> @Nullable T getAttribute(@NotNull AttributeKey<T> key) {
        mutex.readLock().lock();
        try {
            Object value = data.get(key);
            checkValueType(key, value);
            return (T) value;
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public <T> @NotNull @Nullable T getAttributeOrDefault(@NotNull AttributeKey<T> key, @Nullable T defaultValue) {
        checkArgType(key, defaultValue);

        mutex.readLock().lock();
        try {
            Object value = data.get(key);
            checkValueType(key, value);
            return value == null ? defaultValue : ((T) value);
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public <T> void setAttribute(@NotNull AttributeKey<T> key, @Nullable T value) {
        checkArgType(key, value);

        mutex.writeLock().lock();
        try {
            if (value == null) {
                data.remove(key);
            } else {
                data.put(key, value);
            }

            emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, key, value);
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    public <T> @NotNull @Nullable T getAttributeOrSetDefault(@NotNull AttributeKey<T> key, @Nullable T defaultValue) {
        checkArgType(key, defaultValue);

        mutex.writeLock().lock();
        try {
            Object value = data.get(key);
            checkValueType(key, value);

            if (value == null) {
                if (defaultValue == null) {
                    data.remove(key);
                } else {
                    data.put(key, defaultValue);
                }

                emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, key, defaultValue);
                return defaultValue;
            } else {
                return (T) value;
            }
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    public <T> @NotNull T getAttributeOrCreateDefault(@NotNull AttributeKey<T> key, @NotNull Supplier<T> supplier) {
        mutex.writeLock().lock();
        try {
            Object value = data.get(key);
            checkValueType(key, value);

            if (value == null) {
                T defaultValue = supplier.get();

                if (defaultValue == null) {
                    throw new IllegalArgumentException("Default value must not be null");
                } else {
                    checkArgType(key, defaultValue);
                    data.put(key, defaultValue);
                }

                emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, key, defaultValue);
                return defaultValue;
            } else {
                return (T) value;
            }
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    public <T> T getAndUpdate(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        mutex.writeLock().lock();
        try {
            Object value = data.get(key);
            checkValueType(key, value);

            T finalValue = function.apply((T) value);
            if (finalValue == null) {
                data.remove(key);
            } else {
                data.put(key, finalValue);
            }

            emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, key, finalValue);
            return (T) value;
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    public <T> T updateAndGet(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        mutex.writeLock().lock();
        try {
            Object value = data.get(key);
            checkValueType(key, value);

            T finalValue = function.apply((T) value);
            if (finalValue == null) {
                data.remove(key);
            } else {
                data.put(key, finalValue);
            }

            emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, key, finalValue);
            return finalValue;
        } finally {
            mutex.writeLock().unlock();
        }
    }

    @Override
    public <T> @NotNull Optional<T> getAsOptional(@NotNull AttributeKey<T> key) {
        return Optional.ofNullable(getAttribute(key));
    }

    @Override
    public @NotNull ObserverEmitter getObserver() {
        return emitter;
    }

}
