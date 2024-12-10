package dev.tommyjs.jobserve.attribute.impl;

import dev.tommyjs.jobserve.attribute.AttributeKey;
import dev.tommyjs.jobserve.attribute.AttributeRegistry;
import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverEmitter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class AttributeRegistryImpl implements AttributeRegistry, Observable {

    private final ReadWriteLock mutex;
    private final AtomicReference<ObserverEmitter> emitter;
    private final AtomicReference<Map<AttributeKey<?>, Object>> data;

    public AttributeRegistryImpl() {
        this.mutex = new ReentrantReadWriteLock();
        this.emitter = new AtomicReference<>();
        this.data = new AtomicReference<>();
    }

    protected <T> void checkArgType(@NotNull AttributeKey<T> key, @Nullable T value) {
        if (value != null && !key.getType().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Assignment to attribute of type " + key.getType() + " with " +
                "value of type " + value.getClass().getName());
        }
    }

    protected <T> void checkValueType(@NotNull AttributeKey<T> key, @Nullable Object value) {
        if (value != null && !key.getType().isAssignableFrom(value.getClass())) {
            throw new IllegalStateException("Attribute of type " + key.getType() + " contains value " +
                "of type " + value.getClass().getName());
        }
    }

    protected Map<AttributeKey<?>, Object> getData() {
        return data.updateAndGet(map -> Objects.requireNonNullElseGet(map, HashMap::new));
    }

    @Override
    public <T> @Nullable T getAttribute(@NotNull AttributeKey<T> key) {
        mutex.readLock().lock();
        try {
            Object value = getData().get(key);
            checkValueType(key, value);
            return (T) value;
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public <T> @NotNull T getAttributeOrThrow(@NotNull AttributeKey<T> key) {
        mutex.readLock().lock();
        try {
            Object value = getData().get(key);
            if (value == null) {
                throw new IllegalStateException("Attribute not present");
            } else {
                checkValueType(key, value);
                return (T) value;
            }
        } finally {
            mutex.readLock().unlock();
        }
    }

    @Override
    public <T> @NotNull T getAttributeOrDefault(@NotNull AttributeKey<T> key, @NotNull T defaultValue) {
        checkArgType(key, defaultValue);

        mutex.readLock().lock();
        try {
            Object value = getData().get(key);
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
                getData().remove(key);
            } else {
                getData().put(key, value);
            }
        } finally {
            mutex.writeLock().unlock();
        }

        emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, new AttributeUpdate<>(key, value));
    }

    @Override
    public <T> @NotNull T getAttributeOrSetDefault(@NotNull AttributeKey<T> key, @NotNull T defaultValue) {
        checkArgType(key, defaultValue);
        Object value;

        mutex.writeLock().lock();
        try {
            value = getData().get(key);
            checkValueType(key, value);

            if (value == null) {
                getData().put(key, defaultValue);
            }
        } finally {
            mutex.writeLock().unlock();
        }

        if (value == null) {
            value = defaultValue;
            emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, new AttributeUpdate<>(key, defaultValue));
        }

        return (T) value;
    }

    @Override
    public <T> @NotNull T getAttributeOrCreateDefault(@NotNull AttributeKey<T> key, @NotNull Supplier<T> supplier) {
        Object value;
        T defaultValue = null;

        mutex.writeLock().lock();
        try {
            value = getData().get(key);
            checkValueType(key, value);

            if (value == null) {
                defaultValue = supplier.get();
                if (defaultValue == null) {
                    throw new IllegalArgumentException("Default value must not be null");
                } else {
                    checkArgType(key, defaultValue);
                    getData().put(key, defaultValue);
                }

                emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, new AttributeUpdate<>(key, defaultValue));
            }
        } finally {
            mutex.writeLock().unlock();
        }

        if (value == null) {
            value = defaultValue;
            emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, new AttributeUpdate<>(key, defaultValue));
        }

        return (T) value;
    }

    @Override
    public <T> T getAndUpdate(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        Object prev;
        T value;

        mutex.writeLock().lock();
        try {
            prev = getData().get(key);
            checkValueType(key, prev);

            value = function.apply((T) prev);
            if (value == null) {
                getData().remove(key);
            } else {
                getData().put(key, value);
            }
        } finally {
            mutex.writeLock().unlock();
        }

        emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, new AttributeUpdate<>(key, value));

        return (T) prev;
    }

    @Override
    public <T> T updateAndGet(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        T value;

        mutex.writeLock().lock();
        try {
            Object prev = getData().get(key);
            checkValueType(key, prev);

            value = function.apply((T) prev);
            if (value == null) {
                getData().remove(key);
            } else {
                getData().put(key, value);
            }
        } finally {
            mutex.writeLock().unlock();
        }

        emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, new AttributeUpdate<>(key, value));
        return value;
    }

    @Override
    public <T> @NotNull Optional<T> getAsOptional(@NotNull AttributeKey<T> key) {
        return Optional.ofNullable(getAttribute(key));
    }

    @Override
    public @NotNull ObserverEmitter getEmitter() {
        return emitter.updateAndGet(e -> Objects.requireNonNullElseGet(e, ObserverEmitter::create));
    }

}
