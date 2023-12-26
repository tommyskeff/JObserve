package dev.tommyjs.jobserve.observer;

import org.jetbrains.annotations.NotNull;

public abstract class ObservableObject implements Observable {

    private final @NotNull ObserverEmitter emitter;

    public ObservableObject() {
        this.emitter = ObserverEmitter.create();
    }

    @Override
    public @NotNull ObserverEmitter getObserver() {
        return emitter;
    }

}
