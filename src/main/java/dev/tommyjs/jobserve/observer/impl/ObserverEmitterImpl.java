package dev.tommyjs.jobserve.observer.impl;

import dev.tommyjs.jobserve.observer.ObserverEmitter;
import dev.tommyjs.jobserve.observer.ObserverSubscription;
import dev.tommyjs.jobserve.observer.key.DuplexKey;
import dev.tommyjs.jobserve.observer.key.MonoKey;
import dev.tommyjs.jobserve.observer.key.ObserverKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ObserverEmitterImpl implements ObserverEmitter {

    private final Map<ObserverKey, ObserverSet> map;

    public ObserverEmitterImpl() {
        this.map = new ConcurrentHashMap<>();
    }

    protected @NotNull ObserverSubscription observe0(@NotNull ObserverKey key, @NotNull Consumer<Object> consumer, boolean strong) {
        return map.computeIfAbsent(key, _k -> new ObserverSet()).subscribe(consumer, strong);
    }

    @Override
    public <T> @NotNull ObserverSubscription observe(@NotNull MonoKey<T> key, @NotNull Consumer<T> consumer) {
        return observe0(key, o -> consumer.accept(key.getType().cast(o)), true);
    }

    @Override
    public <K, V> @NotNull ObserverSubscription observe(@NotNull DuplexKey<K, V> key, @NotNull BiConsumer<K, V> consumer) {
        return observe0(key, o -> {
            Object[] objects = (Object[]) o;
            K arg1 = key.getType1().cast(objects[0]);
            V arg2 = key.getType2().cast(objects[1]);

            consumer.accept(arg1, arg2);
        }, true);
    }

    @Override
    public @NotNull <T> ObserverSubscription observeWeak(@NotNull MonoKey<T> key, @NotNull Consumer<T> consumer) {
        return observe0(key, o -> key.getType().cast(o), false);
    }

    @Override
    public @NotNull <K, V> ObserverSubscription observeWeak(@NotNull DuplexKey<K, V> key, @NotNull BiConsumer<K, V> consumer) {
        return observe0(key, o -> {
            Object[] objects = (Object[]) o;
            K arg1 = key.getType1().cast(objects[0]);
            V arg2 = key.getType2().cast(objects[1]);

            consumer.accept(arg1, arg2);
        }, false);
    }

    protected void emit0(@NotNull ObserverKey key, @Nullable Object value) {
        ObserverSet set = map.get(key);
        if (set != null) {
            set.call(value);
        }
    }

    @Override
    public <T> void emit(@NotNull MonoKey<T> key, @Nullable T value) {
        emit0(key, value);
    }

    @Override
    public <K, V> void emit(@NotNull DuplexKey<K, V> key, @Nullable K value1, @Nullable V value2) {
        emit0(key, new Object[]{value1, value2});
    }

}
