package dev.tommyjs.jobserve;

import dev.tommyjs.jobserve.dummy.DummyObservable;
import dev.tommyjs.jobserve.dummy.DummyPropertyHolder;
import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.key.ObserverKey;
import dev.tommyjs.jobserve.property.PropertyHolder;
import dev.tommyjs.jobserve.property.PropertyKey;
import dev.tommyjs.jobserve.property.PropertyMap;
import dev.tommyjs.jobserve.util.EmissionWatcher;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ObserverTest {

    @Test
    public void MonoEqualityTest() {
        ObserverKey<Integer> key1 = ObserverKey.register(Integer.class);
        ObserverKey<Integer> key2 = ObserverKey.register(Integer.class);

        assert !key1.equals(key2);
    }

    @Test
    public void MonoEmitTest() {
        Observable observable = new DummyObservable();
        ObserverKey<Integer> integerKey = ObserverKey.register(Integer.class);

        AtomicReference<Integer> ref = new AtomicReference<>();
        observable.observe(integerKey, ref::set);
        observable.emit(integerKey, 50);

        assert ref.get().equals(50);
    }

    @Test
    public void CancelTest() {
        Observable observable = new DummyObservable();
        ObserverKey<Integer> integerKey = ObserverKey.register(Integer.class);

        AtomicReference<Integer> ref = new AtomicReference<>();
        observable.observe(integerKey, ref::set).cancel();
        observable.emit(integerKey, 50);

        assert ref.get() == null;
    }

    @Test
    public void PropertyObserverTest() {
        PropertyHolder holder = new DummyPropertyHolder();
        PropertyKey<Integer> key = PropertyKey.register(Integer.class);

        EmissionWatcher watcher = EmissionWatcher.start(holder, PropertyMap.UPDATE_PROPERTY_KEY, a -> a.key().equals(key.getIdentifier()));
        holder.setProperty(key, 100);

        assert Objects.equals(holder.getProperty(key), 100);
        assert watcher.finish();
    }

}
