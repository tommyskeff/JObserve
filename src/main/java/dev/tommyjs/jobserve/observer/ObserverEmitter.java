package dev.tommyjs.jobserve.observer;

import dev.tommyjs.jobserve.observer.impl.ObserverEmitterImpl;
import dev.tommyjs.jobserve.observer.key.ObserverKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface ObserverEmitter {

    /**
     * Subscribes to all emissions on a specified {@link ObserverKey}. Until {@link ObserverSubscription#cancel()}
     * is called, the specified {@link Consumer} will be called, with an argument of type {@link T},
     * every time there is an emission on this key.
     * <p>
     * This subscription keeps a strong reference to the consumer. The side effect of this behaviour
     * is that the subscriber object cannot be garbage collected until this {@link Observable} has
     * itself been garbage collected.
     * <p>
     * Whilst the strong reference prevents the subscriber from being garbage collected until this
     * object has been garbage collected, the reverse is not true. This object's lifetime is not tied
     * to the lifetime of the subscriber, and may be garbage collected before the subscriber.
     * @param key observer key
     * @param consumer callback to be called upon emission
     * @return cancellable subscription
     * @param <T> emission argument type
     */
    <T> @NotNull ObserverSubscription observe(@NotNull ObserverKey<T> key, @NotNull Consumer<T> consumer);

    /**
     * Emits an event on a specified {@link ObserverKey}. This will immediately call all current
     * subscribers, with the specified value, on the current thread.
     * @param key observer key
     * @param value emission value
     */
    <T> void emit(@NotNull ObserverKey<T> key, @Nullable T value);

    /**
     * Creates a new default {@link ObserverEmitter} instance.
     * @return new observer emitter
     */
    static @NotNull ObserverEmitter create() {
        return new ObserverEmitterImpl();
    }

}
