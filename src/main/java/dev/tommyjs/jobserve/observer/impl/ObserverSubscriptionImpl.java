package dev.tommyjs.jobserve.observer.impl;

import dev.tommyjs.jobserve.observer.ObserverEmitter;
import dev.tommyjs.jobserve.observer.ObserverSubscription;
import dev.tommyjs.jobserve.observer.key.ObserverKey;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ObserverSubscriptionImpl implements ObserverSubscription {

    private final @NotNull ObserverEmitter registry;
    private final int id;
    private final @NotNull ObserverKey key;
    private final @NotNull Consumer<Object> consumer;
    private final @NotNull AtomicReference<Boolean> cancelled;

    protected ObserverSubscriptionImpl(@NotNull ObserverEmitter registry, int id, @NotNull ObserverKey key, @NotNull Consumer<Object> consumer) {
        this.registry = registry;
        this.id = id;
        this.key = key;
        this.consumer = consumer;
        this.cancelled = new AtomicReference<>(false);
    }

    @Override
    public void cancel() {
        cancelled.getAndUpdate(value -> {
            if (!value) registry.cancel(this);
            return true;
        });
    }

    @Override
    public @NotNull ObserverKey getKey() {
        return key;
    }

    @Override
    public @NotNull Consumer<Object> getConsumer() {
        return consumer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObserverSubscriptionImpl that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

}
