package dev.tommyjs.jobserve;

import dev.tommyjs.jobserve.attribute.AttributeHolder;
import dev.tommyjs.jobserve.attribute.AttributeKey;
import dev.tommyjs.jobserve.dummy.DummyAttributeHolder;
import dev.tommyjs.jobserve.util.TypeRef;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AttributeTest {

    @Test
    public void EqualityTest() {
        AttributeKey<Integer> key1 = AttributeKey.register(Integer.class);
        AttributeKey<Integer> key2 = AttributeKey.register(Integer.class);

        assert !key1.equals(key2);
    }

    @Test
    public void HashCodeTest() {
        AttributeKey<Integer> key1 = AttributeKey.register(Integer.class);
        AttributeKey<Integer> key2 = AttributeKey.register(Integer.class);

        assert !Objects.equals(key1.hashCode(), key2.hashCode());
    }

    @Test
    public void NotPresentTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        assert !holder.hasAttribute(integerAttribute);
    }

    @Test
    public void PresentTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        holder.setAttribute(integerAttribute, 100);
        assert holder.hasAttribute(integerAttribute);
    }

    @Test
    public void RetrieveTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();
        holder.setAttribute(integerAttribute, 100);

        assert Objects.equals(holder.getAttribute(integerAttribute), 100);
    }

    @Test
    public void GenericTest() {
        AttributeKey<List<Integer>> listAttribute = AttributeKey.register(new TypeRef<>(){});
        AttributeHolder holder = new DummyAttributeHolder();
        holder.setAttribute(listAttribute, List.of(3, 4, 5));

        List<Integer> a = holder.getAttribute(listAttribute);
        assert a != null && a.size() == 3;
    }

    @Test
    public void OverrideTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();
        holder.setAttribute(integerAttribute, 100);
        holder.setAttribute(integerAttribute, 50);

        assert Objects.equals(holder.getAttribute(integerAttribute), 50);
    }

    @Test
    public void ManualClearTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();
        holder.setAttribute(integerAttribute, 100);
        holder.setAttribute(integerAttribute, null);

        assert !holder.hasAttribute(integerAttribute);
    }

    @Test
    public void ClearTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();
        holder.setAttribute(integerAttribute, 100);
        holder.clearAttribute(integerAttribute);

        assert !holder.hasAttribute(integerAttribute);
    }

    @Test
    public void DefaultTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        assert holder.getAttributeOrDefault(integerAttribute, 100).equals(100);
        assert !holder.hasAttribute(integerAttribute);
    }

    @Test
    public void DefaultSetTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        assert Objects.equals(holder.getAttributeOrSetDefault(integerAttribute, 100), 100);
        assert Objects.equals(holder.getAttribute(integerAttribute), 100);
    }

    @Test
    public void DefaultSupplierTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        assert holder.getAttributeOrCreateDefault(integerAttribute, () -> 100).equals(100);
        assert Objects.equals(holder.getAttribute(integerAttribute), 100);
    }

    @Test
    public void OptionalPresentTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        holder.setAttribute(integerAttribute, 100);
        Optional<Integer> optional = holder.getAttributeAsOptional(integerAttribute);
        assert optional.isPresent() && optional.get().equals(100);
    }

    @Test
    public void OptionalNotPresentTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        Optional<Integer> optional = holder.getAttributeAsOptional(integerAttribute);
        assert optional.isEmpty();
    }

    @Test
    public void GetAndUpdateTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        holder.setAttribute(integerAttribute, 100);

        Integer result = holder.getAttributeAndUpdate(integerAttribute, prev -> {
            assert Objects.equals(prev, 100);
            return 50;
        });
        assert Objects.equals(result, 100);
    }

    @Test
    public void UpdateAndGetTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        holder.setAttribute(integerAttribute, 100);

        Integer result = holder.updateAttributeAndGet(integerAttribute, prev -> {
            assert Objects.equals(prev, 100);
            return 50;
        });
        assert Objects.equals(result, 50);
    }

    @Test
    public void ThrowPresentTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        holder.setAttribute(integerAttribute, 100);
        int result = holder.getAttributeOrThrow(integerAttribute);
        assert Objects.equals(result, 100);
    }

    @Test
    public void ThrowNotPresentTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        assertThrows(IllegalStateException.class, () -> {
            holder.getAttributeOrThrow(integerAttribute);
        });
    }

    @Test
    public void ThrowClearTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        holder.setAttribute(integerAttribute, 100);
        holder.clearAttribute(integerAttribute);

        assertThrows(IllegalStateException.class, () -> {
            holder.getAttributeOrThrow(integerAttribute);
        });
    }

}
