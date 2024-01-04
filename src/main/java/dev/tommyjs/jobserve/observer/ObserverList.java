package dev.tommyjs.jobserve.observer;

import dev.tommyjs.jobserve.attribute.AttributeKey;
import dev.tommyjs.jobserve.attribute.AttributeObservable;
import dev.tommyjs.jobserve.observer.key.DuplexKey;
import dev.tommyjs.jobserve.observer.key.MonoKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ObserverList {

    private final Lock mutex;
    private final List<ObserverSubscription> subscriptions;

    public ObserverList() {
        this.mutex = new ReentrantLock();
        this.subscriptions = new ArrayList<>();
    }

    public <T> @NotNull ObserverSubscription observe(@NotNull Observable object, @NotNull MonoKey<T> key, @NotNull Consumer<T> consumer) {
        ObserverSubscription sub = object.observe(key, consumer);
        add(sub);
        return sub;
    }

    public <K, V> @NotNull ObserverSubscription observe(@NotNull Observable object, @NotNull DuplexKey<K, V> key, @NotNull BiConsumer<K, V> consumer) {
        ObserverSubscription sub = object.observe(key, consumer);
        add(sub);
        return sub;
    }

    public @NotNull <T> ObserverSubscription observeAttribute(@NotNull AttributeObservable object, @NotNull AttributeKey<T> key, @NotNull Consumer<T> consumer) {
        ObserverSubscription sub = object.observeAttribute(key, consumer);
        add(sub);
        return sub;
    }

    public void add(ObserverSubscription subscription) {
        mutex.lock();
        try {
            subscriptions.add(subscription);
        } finally {
            mutex.unlock();
        }
    }

    public void remove(ObserverSubscription subscription) {
        mutex.lock();
        try {
            subscriptions.remove(subscription);
        } finally {
            mutex.unlock();
        }
    }

    public void release(ObserverSubscription subscription) {
        mutex.lock();
        try {
            subscription.cancel();
            subscriptions.remove(subscription);
        } finally {
            mutex.unlock();
        }
    }

    public void releaseAll() {
        mutex.lock();
        try {
            for (ObserverSubscription sub : subscriptions) {
                sub.cancel();
            }

            subscriptions.clear();
        } finally {
            mutex.unlock();
        }
    }

}
