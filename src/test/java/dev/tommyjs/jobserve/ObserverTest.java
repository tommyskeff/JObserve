package dev.tommyjs.jobserve;

import dev.tommyjs.jobserve.attribute.AttributeHolder;
import dev.tommyjs.jobserve.attribute.AttributeKey;
import dev.tommyjs.jobserve.attribute.AttributeRegistry;
import dev.tommyjs.jobserve.dummy.DummyAttributeHolder;
import dev.tommyjs.jobserve.dummy.DummyObservable;
import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.key.ObserverKey;
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
    public void AttributeObserverTest() {
        AttributeHolder holder = new DummyAttributeHolder();
        AttributeKey<Integer> key = AttributeKey.register(Integer.class);

        EmissionWatcher watcher = EmissionWatcher.start(holder, AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, a -> a.key() == key);
        holder.setAttribute(key, 100);

        assert Objects.equals(holder.getAttribute(key), 100);
        assert watcher.finish();
    }

}
