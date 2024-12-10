package dev.tommyjs.jobserve.observer.impl;

import dev.tommyjs.jobserve.observer.ObserverSubscription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ObserverSet {

    private final Set<SubscriptionImpl> subscriptions;
    
    public ObserverSet() {
        this.subscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }
    
    public ObserverSubscription subscribe(@NotNull Consumer<Object> consumer) {
        SubscriptionImpl subscription = new SubscriptionImpl(consumer);
        subscriptions.add(subscription);
        return subscription;
    }
    
    public void call(@Nullable Object object) {
        for (SubscriptionImpl subscription : subscriptions) {
            subscription.call(object);
        }
    }

    private class SubscriptionImpl implements ObserverSubscription {

        private final Consumer<Object> consumer;

        public SubscriptionImpl(Consumer<Object> consumer) {
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
