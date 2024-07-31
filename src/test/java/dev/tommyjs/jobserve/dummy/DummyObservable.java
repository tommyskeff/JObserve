package dev.tommyjs.jobserve.dummy;

import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.ObserverEmitter;
import org.jetbrains.annotations.NotNull;

public class DummyObservable implements Observable {

    private final ObserverEmitter emitter = ObserverEmitter.create();

    @Override
    public @NotNull ObserverEmitter getEmitter() {
        return emitter;
    }

}
