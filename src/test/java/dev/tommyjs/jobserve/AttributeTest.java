package dev.tommyjs.jobserve;

import dev.tommyjs.jobserve.attribute.AttributeHolder;
import dev.tommyjs.jobserve.attribute.AttributeKey;
import dev.tommyjs.jobserve.dummy.DummyAttributeHolder;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Optional;

public class AttributeTest {

    @Test
    public void EqualityTest() {
        AttributeKey<Integer> key1 = AttributeKey.register(Integer.class);
        AttributeKey<Integer> key2 = AttributeKey.register(Integer.class);

        assert !key1.equals(key2);
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

        boolean thrown = false;

        int result = 0;
        try {
            result = holder.getAttributeOrThrow(integerAttribute);
        } catch (IllegalStateException e) {
            thrown = true;
        }

        assert !thrown && Objects.equals(result, 100);
    }

    @Test
    public void ThrowNotPresentTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        boolean thrown = false;

        try {
            holder.getAttributeOrThrow(integerAttribute);
        } catch (IllegalStateException e) {
            thrown = true;
        }

        assert thrown;
    }

    @Test
    public void ThrowClearTest() {
        AttributeKey<Integer> integerAttribute = AttributeKey.register(Integer.class);
        AttributeHolder holder = new DummyAttributeHolder();

        holder.setAttribute(integerAttribute, 100);
        holder.clearAttribute(integerAttribute);

        boolean thrown = false;

        try {
            holder.getAttributeOrThrow(integerAttribute);
        } catch (IllegalStateException e) {
            thrown = true;
        }

        assert thrown;
    }


}
