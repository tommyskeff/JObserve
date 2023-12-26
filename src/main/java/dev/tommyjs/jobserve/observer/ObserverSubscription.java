package dev.tommyjs.jobserve.observer;

import dev.tommyjs.jobserve.observer.key.ObserverKey;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface ObserverSubscription {

    void cancel();

    @NotNull ObserverKey getKey();

    @NotNull Consumer<Object> getConsumer();

}
