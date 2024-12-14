package dev.tommyjs.jobserve.observer.impl;

import dev.tommyjs.jobserve.observer.ObserverSub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ObserverSet {

    private final Set<SubImpl> subscriptions;
    
    public ObserverSet() {
        this.subscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }
    
    public ObserverSub subscribe(@NotNull Consumer<Object> consumer) {
        SubImpl subscription = new SubImpl(consumer);
        subscriptions.add(subscription);
        return subscription;
    }
    
    public void call(@Nullable Object object) {
        for (SubImpl subscription : subscriptions) {
            subscription.call(object);
        }
    }

    private class SubImpl implements ObserverSub {

        private final Consumer<Object> consumer;

        public SubImpl(Consumer<Object> consumer) {
            this.consumer = consumer;
        }

        public void call(@Nullable Object object) {
            consumer.accept(object);
        }

        @Override
        public void cancel() {
            subscriptions.remove(this);
        }

    }

}
