package dev.tommyjs.jobserve.dummy;

import dev.tommyjs.jobserve.attribute.AttributeHolder;
import dev.tommyjs.jobserve.attribute.AttributeRegistry;
import org.jetbrains.annotations.NotNull;

public class DummyAttributeHolder implements AttributeHolder {

    private final AttributeRegistry registry = AttributeRegistry.create();

    @Override
    public @NotNull AttributeRegistry getAttributes() {
        return registry;
    }

}
