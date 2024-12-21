package dev.tommyjs.jobserve;

import com.google.common.reflect.TypeToken;
import dev.tommyjs.jobserve.dummy.DummyPropertyHolder;
import dev.tommyjs.jobserve.property.PropertyHolder;
import dev.tommyjs.jobserve.property.PropertyKey;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PropertyTest {

    @Test
    public void EqualityTest() {
        PropertyKey<Integer> key1 = PropertyKey.register(Integer.class);
        PropertyKey<Integer> key2 = PropertyKey.register(Integer.class);

        assert !key1.equals(key2);
    }

    @Test
    public void HashCodeTest() {
        PropertyKey<Integer> key1 = PropertyKey.register(Integer.class);
        PropertyKey<Integer> key2 = PropertyKey.register(Integer.class);

        assert !Objects.equals(key1.hashCode(), key2.hashCode());
    }

    @Test
    public void NotPresentTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        assert !holder.hasProperty(integerProperty);
    }

    @Test
    public void PresentTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        holder.setProperty(integerProperty, 100);
        assert holder.hasProperty(integerProperty);
    }

    @Test
    public void RetrieveTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();
        holder.setProperty(integerProperty, 100);

        assert Objects.equals(holder.getProperty(integerProperty), 100);
    }

    @Test
    public void GenericTest() {
        PropertyKey<List<Integer>> listProperty = PropertyKey.register(new TypeToken<>(){});
        PropertyHolder holder = new DummyPropertyHolder();
        holder.setProperty(listProperty, List.of(3, 4, 5));

        List<Integer> a = holder.getProperty(listProperty);
        assert a != null && a.size() == 3;
    }

    @Test
    public void OverrideTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();
        holder.setProperty(integerProperty, 100);
        holder.setProperty(integerProperty, 50);

        assert Objects.equals(holder.getProperty(integerProperty), 50);
    }

    @Test
    public void ManualClearTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();
        holder.setProperty(integerProperty, 100);
        holder.setProperty(integerProperty, null);

        assert !holder.hasProperty(integerProperty);
    }

    @Test
    public void ClearTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();
        holder.setProperty(integerProperty, 100);
        holder.clearProperty(integerProperty);

        assert !holder.hasProperty(integerProperty);
    }

    @Test
    public void DefaultTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        assert holder.getPropertyOrDefault(integerProperty, 100).equals(100);
        assert !holder.hasProperty(integerProperty);
    }

    @Test
    public void DefaultSetTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        assert Objects.equals(holder.getPropertyOrSetDefault(integerProperty, 100), 100);
        assert Objects.equals(holder.getProperty(integerProperty), 100);
    }

    @Test
    public void DefaultSupplierTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        assert holder.getPropertyOrCreateDefault(integerProperty, () -> 100).equals(100);
        assert Objects.equals(holder.getProperty(integerProperty), 100);
    }

    @Test
    public void OptionalPresentTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        holder.setProperty(integerProperty, 100);
        Optional<Integer> optional = holder.getPropertyAsOptional(integerProperty);
        assert optional.isPresent() && optional.get().equals(100);
    }

    @Test
    public void OptionalNotPresentTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        Optional<Integer> optional = holder.getPropertyAsOptional(integerProperty);
        assert optional.isEmpty();
    }

    @Test
    public void GetAndUpdateTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        holder.setProperty(integerProperty, 100);

        Integer result = holder.getPropertyAndUpdate(integerProperty, prev -> {
            assert Objects.equals(prev, 100);
            return 50;
        });
        assert Objects.equals(result, 100);
    }

    @Test
    public void UpdateAndGetTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        holder.setProperty(integerProperty, 100);

        Integer result = holder.updatePropertyAndGet(integerProperty, prev -> {
            assert Objects.equals(prev, 100);
            return 50;
        });
        assert Objects.equals(result, 50);
    }

    @Test
    public void ThrowPresentTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        holder.setProperty(integerProperty, 100);
        int result = holder.getPropertyOrThrow(integerProperty);
        assert Objects.equals(result, 100);
    }

    @Test
    public void ThrowNotPresentTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        assertThrows(IllegalStateException.class, () -> {
            holder.getPropertyOrThrow(integerProperty);
        });
    }

    @Test
    public void ThrowClearTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        holder.setProperty(integerProperty, 100);
        holder.clearProperty(integerProperty);

        assertThrows(IllegalStateException.class, () -> {
            holder.getPropertyOrThrow(integerProperty);
        });
    }

    @Test
    public void PropertyCollectionTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);
        PropertyHolder holder = new DummyPropertyHolder();

        holder.setProperty(integerProperty, 100);
        assert holder.getProperties().size() == 1;
    }

    @Test
    public void PropertyCopyTest() {
        PropertyKey<Integer> integerProperty = PropertyKey.register(Integer.class);

        PropertyHolder holder1 = new DummyPropertyHolder();
        PropertyHolder holder2 = new DummyPropertyHolder();

        holder1.setProperty(integerProperty, 100);
        holder2.copyPropertiesFrom(holder1);

        assert Objects.equals(holder2.getProperty(integerProperty), 100);
    }

}
