package dev.tommyjs.jobserve;

import dev.tommyjs.jobserve.attribute.AttributeHolder;
import dev.tommyjs.jobserve.attribute.AttributeKey;
import dev.tommyjs.jobserve.attribute.AttributeRegistry;
import dev.tommyjs.jobserve.dummy.DummyAttributeHolder;
import dev.tommyjs.jobserve.dummy.DummyObservable;
import dev.tommyjs.jobserve.observer.Observable;
import dev.tommyjs.jobserve.observer.key.DuplexKey;
import dev.tommyjs.jobserve.observer.key.MonoKey;
import dev.tommyjs.jobserve.util.EmissionWatcher;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObserverTest {

    @Test
    public void MonoEqualityTest() {
        MonoKey<Integer> key1 = MonoKey.register(Integer.class);
        MonoKey<Integer> key2 = MonoKey.register(Integer.class);

        assert !key1.equals(key2);
    }

    @Test
    public void DuplexEqualityTest() {
        DuplexKey<Integer, Integer> key1 = DuplexKey.register(Integer.class, Integer.class);
        DuplexKey<Integer, Integer> key2 = DuplexKey.register(Integer.class, Integer.class);

        assert !key1.equals(key2);
    }

    @Test
    public void MonoEmitTest() {
        Observable observable = new DummyObservable();
        MonoKey<Integer> integerKey = MonoKey.register(Integer.class);

        AtomicReference<Integer> ref = new AtomicReference<>();
        observable.observe(integerKey, ref::set);
        observable.emit(integerKey, 50);

        assert ref.get().equals(50);
    }

    @Test
    public void DuplexEmitTest() {
        Observable observable = new DummyObservable();
        DuplexKey<Integer, String> duplexKey =  DuplexKey.register(Integer.class, String.class);

        AtomicReference<Integer> intRef = new AtomicReference<>();
        AtomicReference<String> strRef = new AtomicReference<>();

        observable.observe(duplexKey, (i, s) -> {
            intRef.set(i);
            strRef.set(s);
        });

        observable.emit(duplexKey, 50, "hello world");

        assert intRef.get().equals(50);
        assert strRef.get().equals("hello world");
    }

    @Test
    public void CancelTest() {
        Observable observable = new DummyObservable();
        MonoKey<Integer> integerKey = MonoKey.register(Integer.class);

        AtomicReference<Integer> ref = new AtomicReference<>();
        observable.observe(integerKey, ref::set).cancel();
        observable.emit(integerKey, 50);

        assert ref.get() == null;
    }

    @Test
    public void WeakCancelTest() {
        Observable observable = new DummyObservable();
        MonoKey<Integer> integerKey = MonoKey.register(Integer.class);

        AtomicReference<Integer> ref = new AtomicReference<>();
        observable.observeWeak(integerKey, ref::set).cancel();
        observable.emit(integerKey, 50);

        assert ref.get() == null;
    }

    @Test
    public void AttributeObserverTest() {
        AttributeHolder holder = new DummyAttributeHolder();
        AttributeKey<Integer> key = AttributeKey.register(Integer.class);

        EmissionWatcher watcher = EmissionWatcher.start(holder, AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER, (a, o) -> a == key);
        holder.setAttribute(key, 100);

        assert Objects.equals(holder.getAttribute(key), 100);
        assert watcher.finish();
    }

    @Test
    public void WeakTest() {
        Observable observable = new DummyObservable();
        MonoKey<Integer> integerKey = MonoKey.register(Integer.class);
        WeakReference<Consumer<Integer>> consumer = new WeakReference<>(System.out::println);

        assert consumer.get() != null;
        observable.observeWeak(integerKey, Objects.requireNonNull(consumer.get()));

        System.gc();
        assert consumer.get() == null;
    }

}
