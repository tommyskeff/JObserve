package dev.tommyjs.jobserve.observer;

import dev.tommyjs.jobserve.observer.key.ObserverKey;

/**
 * Represents the result of subscribing to a {@link ObserverKey} on a {@link Observable} object.
 */
public interface ObserverSub {

    /**
     * Cancels this subscription. It is guaranteed that following execution of this method, the
     * associated callback will never be called again.
     * <p>
     * If this subscription was created with a strong reference, this will also release the
     * reference and the subscriber object will be eligible for garbage collection (provided no
     * other references still remain).
     */
    void cancel();

}
