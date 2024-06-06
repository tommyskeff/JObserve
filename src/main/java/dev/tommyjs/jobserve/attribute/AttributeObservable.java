package dev.tommyjs.jobserve.attribute;

import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverSubscription;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A marker interface signifying that this object has attributes which can be subscribed to, in
 * order to be notified of updates to attributes.
 */
public interface AttributeObservable extends Observable {

    /**
     * Subscribes to mutations of a given {@link AttributeKey} on an {@link Observable} with attributes.
     * @param key attribute key
     * @param consumer callback
     * @return cancellable subscription
     */
    default @NotNull <T> ObserverSubscription observeAttribute(@NotNull AttributeKey<T> key, @NotNull Consumer<T> consumer) {
        return observe(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, (k, o) -> {
            if (k == key) {
                //noinspection unchecked
                consumer.accept((T) o);
            }
        });
    }

}
