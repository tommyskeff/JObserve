package dev.tommyjs.jobserve.util;

import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverSub;
import dev.tommyjs.jobserve.observer.key.ObserverKey;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * A utility to capture observer emissions between two points in a code sequence. The recommended
 * usage of this class is to first obtain a watcher for a specific {@link ObserverKey} on a specific
 * {@link Observable} object with {@link EmissionWatcher#start}. Then at some later point in time,
 * {@link EmissionWatcher#finish()} can be called, and this will return whether there was an emission
 * between starting the watcher and now.]
 */
public final class EmissionWatcher {

    private final @NotNull ObserverSub subscription;
    private final @NotNull AtomicBoolean trip;
    private final @NotNull AtomicBoolean finished = new AtomicBoolean();

    private EmissionWatcher(@NotNull ObserverSub subscription, @NotNull AtomicBoolean trip) {
        this.subscription = subscription;
        this.trip = trip;
    }

    /**
     * Terminates this watcher and returns whether there was an emission between starting the watcher
     * and now. This method will cancel the {@link ObserverSub} associated with this watcher.
     * @return whether the watcher was triggered
     */
    public boolean finish() {
        if (!finished.compareAndSet(false, true)) {
            throw new IllegalStateException("Emission watcher already finished");
        }

        subscription.cancel();
        return trip.get();
    }

    /**
     * Starts a new emission watcher with a given observed object, key, and trigger filter. This
     * method will immediately subscribe. Emissions from the object will only be accepted if they
     * pass the given trigger filter.
     * @param object observable object
     * @param key observer key to subscribe to
     * @param filter trigger filter
     * @return started emission watcher
     */
    public static <T> EmissionWatcher start(@NotNull Observable object, @NotNull ObserverKey<T> key, @NotNull Predicate<T> filter) {
        AtomicBoolean trip = new AtomicBoolean();
        ObserverSub sub = object.observe(key, v -> {
            if (filter.test(v)) trip.set(true);
        });

        return new EmissionWatcher(sub, trip);
    }

    /**
     * Starts a new emission watcher with a given observed object, key, and default trigger filter.
     * This method will immediately subscribe. Emissions from the object will always be accepted.
     * @param object observable object
     * @param key observer key to subscribe to
     * @return started emission watcher
     */
    public static <T> EmissionWatcher start(@NotNull Observable object, @NotNull ObserverKey<T> key) {
        return start(object, key, _t -> true);
    }

}
