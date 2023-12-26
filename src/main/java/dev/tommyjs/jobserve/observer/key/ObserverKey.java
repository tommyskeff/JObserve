package dev.tommyjs.jobserve.observer.key;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

public class ObserverKey {

    protected static @NotNull Random RANDOM = new SecureRandom();

    private final int keyId;

    public ObserverKey(int keyId) {
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
