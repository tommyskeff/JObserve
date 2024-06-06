package dev.tommyjs.jobserve.observer;

import dev.tommyjs.jobserve.observer.key.DuplexKey;
import dev.tommyjs.jobserve.observer.key.MonoKey;
import dev.tommyjs.jobserve.observer.key.ObserverKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * An {@link Observable} object can be observed, which essentially means "events" can be subscribed
 * to, which will be "emitted" from this object.
 * <p>
 * {@link ObserverKey} objects act as "channels", and are essentially a namespace to emit events
 * to. Subscribers can subscribe to specific keys depending on what the desired event is to
 * receive.
 * <p>
 * This is useful in avoiding direct coupling between application components, and in some cases
 * circular dependencies. It allows for reacting to internal state changes in an object, without
 * directly injecting the reacting object into this object. This keeps code maintainable, testable,
 * and helps to avoid bad design practices as described above.
 */
public interface Observable {

    /**
     * Retrieves the {@link ObserverEmitter} instance powering this {@link Observable}. This method
     * should always return the same instance.
     * @return observer emitter powering this object
     */
    @NotNull ObserverEmitter getObserver();

    /**
     * Subscribes to all emissions on a specified {@link ObserverKey}. Until {@link ObserverSubscription#cancel()}
     * is called, the specified {@link Consumer} will be called, with an argument of type {@link T},
     * every time there is an emission on this key.
     * <p>
     * This subscription keeps a strong reference to the consumer. The side effect of this behaviour
     * is that the subscriber object cannot be garbage collected until this {@link Observable} has
     * itself been garbage collected. If this is not the desired behaviour, you may wish to consider
     * {@link Observable#observeWeak(MonoKey, Consumer)} alternatively.
     * <p>
     * Whilst the strong reference prevents the subscriber from being garbage collected until this
     * object has been garbage collected, the reverse is not true. This object's lifetime is not tied
     * to the lifetime of the subscriber, and may be garbage collected before the subscriber.
     * @param key observer key
     * @param consumer callback to be called upon emission
     * @return cancellable subscription
     * @param <T> emission argument type
     */
    default <T> @NotNull ObserverSubscription observe(@NotNull MonoKey<T> key, @NotNull Consumer<T> consumer) {
        return getObserver().observe(key, consumer);
    }

    /**
     * Subscribes to all emissions on a specified {@link ObserverKey}. Until {@link ObserverSubscription#cancel()}
     * is called, the specified {@link Consumer} will be called, with two arguments of types
     * {@link K} and {@link V}, every time there is an emission on this key.
     * <p>
     * This subscription keeps a strong reference to the consumer. The side effect of this behaviour
     * is that the subscriber object cannot be garbage collected until this {@link Observable} has
     * itself been garbage collected. If this is not the desired behaviour, you may wish to consider
     * {@link Observable#observeWeak(DuplexKey, BiConsumer)} alternatively.
     * <p>
     * Whilst the strong reference prevents the subscriber from being garbage collected until this
     * object has been garbage collected, the reverse is not true. This object's lifetime is not tied
     * to the lifetime of the subscriber, and may be garbage collected before the subscriber.
     * @param key observer key
     * @param consumer callback to be called upon emission
     * @return cancellable subscription
     * @param <K> 1st emission argument type
     * @param <V> 2nd emission argument type
     */
    default <K, V> @NotNull ObserverSubscription observe(@NotNull DuplexKey<K, V> key, @NotNull BiConsumer<K, V> consumer) {
        return getObserver().observe(key, consumer);
    }

    /**
     * Subscribes to all emissions on a specified {@link ObserverKey}. Until {@link ObserverSubscription#cancel()}
     * is called, the specified {@link Consumer} will be called, with an argument of type {@link T},
     * every time there is an emission on this key.
     * <p>
     * This subscription keeps a weak reference to the consumer. This means that both the subscriber
     * and {@link Observable} object can be garbage collected without the other having already been
     * garbage collected. This can be useful to avoid memory leaks, but care must be taken when using
     * this method to avoid accidental garbage collections of subscribers if they have no external
     * references when not intended.
     * @param key observer key
     * @param consumer callback to be called upon emission
     * @return cancellable subscription
     * @param <T> emission argument type
     */
    default <T> @NotNull ObserverSubscription observeWeak(@NotNull MonoKey<T> key, @NotNull Consumer<T> consumer) {
        return getObserver().observe(key, consumer);
    }

    /**
     * Subscribes to all emissions on a specified {@link ObserverKey}. Until {@link ObserverSubscription#cancel()}
     * is called, the specified {@link Consumer} will be called, with two arguments of types
     * {@link K} and {@link V}, every time there is an emission on this key.
     * <p>
     * This subscription keeps a weak reference to the consumer. This means that both the subscriber
     * and {@link Observable} object can be garbage collected without the other having already been
     * garbage collected. This can be useful to avoid memory leaks, but care must be taken when using
     * this method to avoid accidental garbage collections of subscribers if they have no external
     * references when not intended.
     * @param key observer key
     * @param consumer callback to be called upon emission
     * @param <K> 1st emission argument type
     * @param <V> 2nd emission argument type
     */
    default <K, V> @NotNull ObserverSubscription observeWeak(@NotNull DuplexKey<K, V> key, @NotNull BiConsumer<K, V> consumer) {
        return getObserver().observe(key, consumer);
    }

    /**
     * Emits an event on a specified {@link ObserverKey}. This will immediately call all current
     * subscribers, with the specified value, on the current thread.
     * @param key observer key
     * @param value emission value
     */
    default <T> void emit(@NotNull MonoKey<T> key, @Nullable T value) {
        getObserver().emit(key, value);
    }

    /**
     * Emits an event on a specified {@link ObserverKey}. This will immediately call all current
     * subscribers, with the specified values, on the current thread.
     * @param key observer key
     * @param value1 1st emission value
     * @param value2 2nd emission value
     */
    default <K, V> void emit(@NotNull DuplexKey<K, V> key, @Nullable K value1, @Nullable V value2) {
        getObserver().emit(key, value1, value2);
    }

}
