package dev.tommyjs.jobserve.observer.key;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public sealed class ObserverKey permits DuplexKey, MonoKey {

    protected static final AtomicInteger INDEX = new AtomicInteger();

    private final int keyId;

    protected ObserverKey(int keyId) {
        this.keyId = keyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObserverKey that)) return false;
        return keyId == that.keyId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyId);
    }

}
