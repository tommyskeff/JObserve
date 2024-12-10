package dev.tommyjs.jobserve.observer.impl;

import dev.tommyjs.jobserve.observer.ObserverEmitter;
import dev.tommyjs.jobserve.observer.ObserverSubscription;
import dev.tommyjs.jobserve.observer.key.ObserverKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ObserverEmitterImpl implements ObserverEmitter {

    private final Map<ObserverKey, ObserverSet> map;

    public ObserverEmitterImpl() {
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public <T> @NotNull ObserverSubscription observe(@NotNull ObserverKey<T> key, @NotNull Consumer<T> consumer) {
        return map.computeIfAbsent(key, _k -> new ObserverSet()).subscribe(o -> consumer.accept((T) o));
    }

    @Override
    public <T> void emit(@NotNull ObserverKey<T> key, @Nullable T value) {
        ObserverSet set = map.get(key);
        if (set != null) {
            set.call(value);
        }
    }

}
