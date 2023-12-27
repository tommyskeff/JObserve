package dev.tommyjs.jobserve.observer.impl;

import dev.tommyjs.jobserve.observer.ObserverEmitter;
import dev.tommyjs.jobserve.observer.ObserverSubscription;
import dev.tommyjs.jobserve.observer.key.DuplexKey;
import dev.tommyjs.jobserve.observer.key.MonoKey;
import dev.tommyjs.jobserve.observer.key.ObserverKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ObserverEmitterImpl implements ObserverEmitter {

    private final Random random;
    private final @NotNull Map<ObserverKey, AtomicReference<Set<ObserverSubscription>>> observers;

    public ObserverEmitterImpl() {
        this.random = new SecureRandom();
        this.observers = new ConcurrentHashMap<>();
    }

    protected @NotNull ObserverSubscription observe0(@NotNull ObserverKey key, @NotNull Consumer<Object> consumer) {
        ObserverSubscription subscription = new ObserverSubscriptionImpl(this, random.nextInt(), key, consumer);

        observers.compute(key, (_k, ref) -> {
            if (ref == null) {
                ref = new AtomicReference<>(new HashSet<>());
            }

            ref.get().add(subscription);
            return ref;
        });

        return subscription;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> @NotNull ObserverSubscription observe(@NotNull MonoKey<T> key, @NotNull Consumer<T> consumer) {
        return observe0(key, o -> consumer.accept((T) o));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <K, V> @NotNull ObserverSubscription observe(@NotNull DuplexKey<K, V> key, @NotNull BiConsumer<K, V> consumer) {
        return observe0(key, o -> {
            Object[] objects = (Object[]) o;
            consumer.accept((K) objects[0], (V) objects[1]);
        });
    }

    protected void emit0(@NotNull ObserverKey key, @Nullable Object value) {
        observers.compute(key, (_k, ref) -> {
            if (ref == null) {
                return null;
            }

            for (ObserverSubscription subscription : ref.get()) {
                subscription.getConsumer().accept(value);
            }

            return ref;
        });
    }

    @Override
    public <T> void emit(@NotNull MonoKey<T> key, @Nullable T value) {
        emit0(key, value);
    }

    @Override
    public <K, V> void emit(@NotNull DuplexKey<K, V> key, @Nullable K value1, @Nullable V value2) {
        emit0(key, new Object[]{value1, value2});
    }

    @Override
    public void cancel(@NotNull ObserverSubscription subscription) {
        observers.compute(subscription.getKey(), (key, ref) -> {
            if (ref != null) {
                ref.get().remove(subscription);
            }

            return ref;
        });
    }

}
