package dev.tommyjs.jobserve.attribute.impl;

import dev.tommyjs.jobserve.attribute.AttributeKey;
import dev.tommyjs.jobserve.attribute.AttributeRegistry;
import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverEmitter;
import dev.tommyjs.jobserve.observer.ObserverSubscription;
import dev.tommyjs.jobserve.observer.impl.ObserverEmitterImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AttributeRegistryImpl implements AttributeRegistry, Observable {

    private final ObserverEmitter emitter;
    private final Map<AttributeKey<?>, AtomicReference<Object>> data;

    public AttributeRegistryImpl() {
        this.emitter = new ObserverEmitterImpl();
        this.data = new ConcurrentHashMap<>();
    }

    protected @NotNull AtomicReference<Object> getReference(@NotNull AttributeKey<?> key) {
        return data.computeIfAbsent(key, attributeKey -> new AtomicReference<>());
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> @Nullable T getAttribute(@NotNull AttributeKey<T> key) {
        Object value = getReference(key).get();
        checkValueType(key, value);
        return (T) value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @Nullable T getAttributeOrDefault(@NotNull AttributeKey<T> key, @Nullable T defaultValue) {
        checkArgType(key, defaultValue);
        Object value = getReference(key).get();
        checkValueType(key, value);
        return value == null ? defaultValue : ((T) value);
    }

    @Override
    public <T> void setAttribute(@NotNull AttributeKey<T> key, @Nullable T value) {
        checkArgType(key, value);
        AtomicReference<Object> ref = getReference(key);
        ref.set(value);

        emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @Nullable T getAttributeOrSetDefault(@NotNull AttributeKey<T> key, @Nullable T defaultValue) {
        checkArgType(key, defaultValue);
        AtomicReference<Object> ref = getReference(key);

        AtomicReference<T> finalVal = new AtomicReference<>();
        AtomicBoolean changed = new AtomicBoolean();

        ref.getAndUpdate(o -> {
            checkValueType(key, o);
            if (o == null) {
                changed.set(true);
                finalVal.set(defaultValue);
                return defaultValue;
            } else {
                finalVal.set((T) o);
                return o;
            }
        });

        if (changed.get()) {
            emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, key, defaultValue);
        }

        return finalVal.get();
    }

    @Override
    public <T> @NotNull @Nullable T getAttributeOrCreateDefault(@NotNull AttributeKey<T> key, @NotNull Supplier<T> supplier) {
        AtomicReference<Object> ref = getReference(key);

        AtomicReference<T> finalVal = new AtomicReference<>();
        AtomicBoolean changed = new AtomicBoolean();

        ref.getAndUpdate(o -> {
            checkValueType(key, o);
            if (o == null) {
                if (finalVal.get() == null) {
                    T res = supplier.get();
                    checkArgType(key, res);

                    finalVal.set(res);
                    changed.set(true);

                    o = res;
                } else {
                    o = finalVal.get();
                }
            }

            return o;
        });

        if (changed.get()) {
            emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, key, finalVal.get());
        }

        return finalVal.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAndUpdate(@NotNull AttributeKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        AtomicReference<Object> ref = getReference(key);
        AtomicReference<T> res = new AtomicReference<>();

        ref.getAndUpdate(value -> {
            checkValueType(key, value);
            T result = function.apply((T) value);
            checkArgType(key, result);
            res.set(result);
            return result;
        });

        emit(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, key, res.get());

        return res.get();
    }

    @Override
    public <T> Optional<T> getAsOptional(@NotNull AttributeKey<T> key) {
        return Optional.ofNullable(getAttribute(key));
    }


    @Override
    public @NotNull ObserverEmitter getObserver() {
        return emitter;
    }

}
