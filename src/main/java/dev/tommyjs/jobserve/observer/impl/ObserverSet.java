package dev.tommyjs.jobserve.observer.impl;

import dev.tommyjs.jobserve.observer.ObserverSubscription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ObserverSet {

    private final Set<CallableSubscription> subscriptions;
    
    public ObserverSet() {
        this.subscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }
    
    public ObserverSubscription subscribe(@NotNull Consumer<Object> consumer, boolean strong) {
        CallableSubscription subscription = strong ? new StrongSubscription(consumer) : new WeakSubscription(consumer);
        subscriptions.add(subscription);
        return subscription;
    }
    
    public void call(@Nullable Object object) {
        for (CallableSubscription subscription : subscriptions) {
            subscription.call(object);
        }
    }

    private interface CallableSubscription extends ObserverSubscription {

        void call(@Nullable Object object);

    }

    private class StrongSubscription implements CallableSubscription {

        private final Consumer<Object> consumer;

        public StrongSubscription(Consumer<Object> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void call(@Nullable Object object) {
            consumer.accept(object);
        }

        @Override
        public void cancel() {
            subscriptions.remove(this);
        }

    }

    private class WeakSubscription implements CallableSubscription {

        private final WeakReference<Consumer<Object>> consumerRef;

        public WeakSubscription(Consumer<Object> consumer) {
            this.consumerRef = new WeakReference<>(consumer);
        }

        @Override
        public void call(@Nullable Object object) {
            Consumer<Object> consumer = consumerRef.get();
            if (consumer == null) {
                cancel();
            } else {
                consumer.accept(object);
            }
        }

        @Override
        public void cancel() {
            if (consumerRef.get() != null) {
                consumerRef.clear();
            }

            subscriptions.remove(this);
        }

    }

}
