package dev.tommyjs.jobserve.observer.impl;

import dev.tommyjs.jobserve.observer.ObserverSubscription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class ObserverSet {

    private final ReadWriteLock rwLock;
    private final Set<Subscription> strongSubscriptions;
    private final Set<Subscription> weakSubscriptions;
    
    public ObserverSet() {
        this.rwLock = new ReentrantReadWriteLock();
        this.strongSubscriptions = new HashSet<>();
        this.weakSubscriptions = Collections.newSetFromMap(new WeakHashMap<>());
    }
    
    public ObserverSubscription subscribe(@NotNull Consumer<Object> consumer, boolean strong) {
        Subscription subscription = new Subscription(consumer, strong);

        rwLock.writeLock().lock();
        try {
            (strong ? strongSubscriptions : weakSubscriptions).add(subscription);
        } finally {
            rwLock.writeLock().unlock();
        }

        return subscription;
    }
    
    public void call(@Nullable Object object) {
        rwLock.readLock().lock();
        try {
            for (Subscription subscription : strongSubscriptions) subscription.call(object);
            for (Subscription subscription : weakSubscriptions) subscription.call(object);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    private class Subscription implements ObserverSubscription {

        private final Consumer<Object> consumer;
        private final boolean strong;

        public Subscription(Consumer<Object> consumer, boolean strong) {
            this.consumer = consumer;
            this.strong = strong;
        }

        public void call(@Nullable Object object) {
            consumer.accept(object);
        }

        @Override
        public void cancel() {
            rwLock.writeLock().lock();
            try {
                (strong ? strongSubscriptions : weakSubscriptions).remove(this);
            } finally {
                rwLock.writeLock().unlock();
            }
        }

    }

}
