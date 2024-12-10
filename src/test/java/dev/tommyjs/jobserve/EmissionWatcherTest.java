package dev.tommyjs.jobserve;

import dev.tommyjs.jobserve.dummy.DummyObservable;
import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.key.ObserverKey;
import dev.tommyjs.jobserve.util.EmissionWatcher;
import org.junit.jupiter.api.Test;

public class EmissionWatcherTest {

    @Test
    public void NoTriggerTest() {
        Observable observable = new DummyObservable();
        ObserverKey<Integer> integerKey = ObserverKey.register(Integer.class);

        EmissionWatcher watcher = EmissionWatcher.start(observable, integerKey);
        assert !watcher.finish();
    }

    @Test
    public void TriggerTest() {
        Observable observable = new DummyObservable();
        ObserverKey<Integer> integerKey = ObserverKey.register(Integer.class);

        EmissionWatcher watcher = EmissionWatcher.start(observable, integerKey);
        observable.emit(integerKey, 100);

        assert watcher.finish();
    }

    @Test
    public void InvalidTriggerTest() {
        Observable observable = new DummyObservable();
        ObserverKey<Integer> integerKey = ObserverKey.register(Integer.class);

        EmissionWatcher watcher = EmissionWatcher.start(observable, integerKey, i -> i > 50);
        observable.emit(integerKey, 20);

        assert !watcher.finish();
    }

    @Test
    public void ValidTriggerTest() {
        Observable observable = new DummyObservable();
        ObserverKey<Integer> integerKey = ObserverKey.register(Integer.class);

        EmissionWatcher watcher = EmissionWatcher.start(observable, integerKey, i -> i > 50);
        observable.emit(integerKey, 75);

        assert watcher.finish();
    }

}
