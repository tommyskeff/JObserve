package dev.tommyjs.jobserve.property.impl;

import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverEmitter;
import dev.tommyjs.jobserve.property.PropertyEntry;
import dev.tommyjs.jobserve.property.PropertyKey;
import dev.tommyjs.jobserve.property.PropertyMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class PropertyMapImpl implements PropertyMap, Observable {

    private final AtomicReference<ObserverEmitter> emitter;
    private final AtomicReference<Map<String, Object>> data;

    public PropertyMapImpl() {
        this.emitter = new AtomicReference<>();
        this.data = new AtomicReference<>();
    }

    public PropertyMapImpl(@NotNull ObserverEmitter emitter) {
        this();
        this.emitter.set(emitter);
    }

    protected Map<String, Object> getData(boolean createIfAbsent) {
        return createIfAbsent ?
            data.updateAndGet(map -> Objects.requireNonNullElseGet(map, ConcurrentHashMap::new)) :
            Objects.requireNonNullElseGet(data.get(), Collections::emptyMap);
    }

    @Override
    public <T> @Nullable T getProperty(@NotNull PropertyKey<T> key) {
        return (T) getProperty(key.getIdentifier());
    }

    @Override
    public @Nullable Object getProperty(@NotNull String key) {
        Map<String, Object> data = getData(false);
        return data.get(key);
    }

    @Override
    public <T> @NotNull T getPropertyOrThrow(@NotNull PropertyKey<T> key) {
        return (T) getPropertyOrThrow(key.getIdentifier());
    }

    @Override
    public @NotNull Object getPropertyOrThrow(@NotNull String key) {
        Object value = getData(false).get(key);
        if (value == null) {
            throw new IllegalStateException("Property not present");
        } else {
            return value;
        }
    }

    @Override
    public <T> @NotNull T getPropertyOrDefault(@NotNull PropertyKey<T> key, @NotNull T defaultValue) {
        return (T) getPropertyOrDefault(key.getIdentifier(), defaultValue);
    }

    @Override
    public @NotNull Object getPropertyOrDefault(@NotNull String key, @NotNull Object defaultValue) {
        Object value = getData(false).get(key);
        return value == null ? defaultValue : value;
    }

    @Override
    public <T> void setProperty(@NotNull PropertyKey<T> key, @Nullable T value) {
        setProperty(key.getIdentifier(), value);
    }

    @Override
    public void setProperty(@NotNull String key, @Nullable Object value) {
        if (value == null) {
            Object prev = getData(true).remove(key);
            emit(PropertyMap.UPDATE_PROPERTY_KEY, new PropertyUpdate(key, prev, null));
        } else {
            Object prev = getData(true).put(key, value);
            emit(PropertyMap.UPDATE_PROPERTY_KEY, new PropertyUpdate(key, prev, value));
        }
    }

    @Override
    public <T> @NotNull T getPropertyOrSetDefault(@NotNull PropertyKey<T> key, @NotNull T defaultValue) {
        return (T) getPropertyOrSetDefault(key.getIdentifier(), defaultValue);
    }

    @Override
    public @NotNull Object getPropertyOrSetDefault(@NotNull String key, @NotNull Object defaultValue) {
        Object value = getData(true).putIfAbsent(key, defaultValue);
        if (value == null) {
            value = defaultValue;
            emit(PropertyMap.UPDATE_PROPERTY_KEY, new PropertyUpdate(key, null, defaultValue));
        }

        return value;
    }

    @Override
    public <T> @NotNull T getPropertyOrCreateDefault(@NotNull PropertyKey<T> key, @NotNull Supplier<T> supplier) {
        return (T) getPropertyOrCreateDefault(key.getIdentifier(), (Supplier<Object>) supplier);
    }

    @Override
    public @NotNull Object getPropertyOrCreateDefault(@NotNull String key, @NotNull Supplier<@NotNull Object> supplier) {
        AtomicBoolean updated = new AtomicBoolean(false);
        Object value = getData(true).computeIfAbsent(key, k -> {
            Object obj = supplier.get();
            updated.set(true);
            return obj;
        });

        if (updated.get()) {
            emit(PropertyMap.UPDATE_PROPERTY_KEY, new PropertyUpdate(key, null, value));
        }

        return value;
    }

    @Override
    public <T> T getPropertyAndUpdate(@NotNull PropertyKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        return (T) getPropertyAndUpdate(key.getIdentifier(), (Function<Object, Object>) function);
    }

    @Override
    public @Nullable Object getPropertyAndUpdate(@NotNull String key, @NotNull Function<@Nullable Object, @Nullable Object> function) {
        AtomicReference<Object> prev = new AtomicReference<>();
        Object value = getData(true).compute(key, (k, v) -> {
            prev.set(v);
            return function.apply(v);
        });

        emit(PropertyMap.UPDATE_PROPERTY_KEY, new PropertyUpdate(key, prev.get(), value));
        return prev.get();
    }

    @Override
    public <T> T updatePropertyAndGet(@NotNull PropertyKey<T> key, @NotNull Function<@Nullable T, @Nullable T> function) {
        return (T) updatePropertyAndGet(key.getIdentifier(), (Function<Object, Object>) function);
    }

    @Override
    public @Nullable Object updatePropertyAndGet(@NotNull String key, @NotNull Function<@Nullable Object, @Nullable Object> function) {
        AtomicReference<Object> prev = new AtomicReference<>();
        Object curr = getData(true).compute(key, (k, v) -> {
            prev.set(v);
            return function.apply(v);
        });

        emit(PropertyMap.UPDATE_PROPERTY_KEY, new PropertyUpdate(key, prev.get(), curr));
        return curr;
    }

    @Override
    public <T> @NotNull Optional<T> getPropertyAsOptional(@NotNull PropertyKey<T> key) {
        return (Optional<T>) getPropertyAsOptional(key.getIdentifier());
    }

    @Override
    public @NotNull Optional<Object> getPropertyAsOptional(@NotNull String key) {
        return Optional.ofNullable(getData(true).get(key));
    }

    @Override
    public @NotNull Collection<PropertyEntry> getProperties() {
        return new EntryCollection();
    }

    @Override
    public void copyInto(@NotNull PropertyMap target) {
        getData(false).forEach(target::setProperty);
    }

    @Override
    public void clear() {
        data.set(null);
    }

    @Override
    public @NotNull ObserverEmitter getEmitter() {
        return emitter.updateAndGet(e -> Objects.requireNonNullElseGet(e, ObserverEmitter::create));
    }

    private class EntryCollection extends AbstractCollection<PropertyEntry> {

        @Override
        public int size() {
            return getData(false).size();
        }

        @Override
        public boolean isEmpty() {
            return getData(false).isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof PropertyEntry(String key, Object obj)) {
                Object val = getData(false).get(key);
                return Objects.equals(obj, val);
            } else {
                return false;
            }
        }

        @Override
        public @NotNull Iterator<PropertyEntry> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean add(PropertyEntry entry) {
            setProperty(entry.key(), entry.data());
            return true;
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof PropertyEntry(String key, Object obj)) {
                return getData(false).compute(key, (k, v) -> Objects.equals(v, obj) ? null : v) == null;
            } else {
                return false;
            }
        }

        @Override
        public void clear() {
            PropertyMapImpl.this.clear();
        }

    }

    private class EntryIterator implements Iterator<PropertyEntry> {

        private final Iterator<Map.Entry<String, Object>> iterator = getData(false).entrySet().iterator();

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public PropertyEntry next() {
            Map.Entry<String, Object> entry = iterator.next();
            return entry == null ? null : new PropertyEntry(entry.getKey(), entry.getValue());
        }

    }

}
