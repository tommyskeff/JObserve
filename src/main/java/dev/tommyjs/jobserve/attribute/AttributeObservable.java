package dev.tommyjs.jobserve.attribute;

import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverSubscription;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface AttributeObservable extends Observable {

    default @NotNull <T> ObserverSubscription observeAttribute(@NotNull AttributeKey<T> key, @NotNull Consumer<T> consumer) {
        return observe(AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, (k, o) -> {
            if (k == key) {
                //noinspection unchecked
                consumer.accept((T) o);
            }
        });
    }

}
