package dev.tommyjs.jobserve.dummy;

import dev.tommyjs.jobserve.property.PropertyHolder;
import dev.tommyjs.jobserve.property.PropertyMap;
import org.jetbrains.annotations.NotNull;

public class DummyPropertyHolder implements PropertyHolder {

    private final PropertyMap registry = PropertyMap.create();

    @Override
    public @NotNull PropertyMap getPropertyMap() {
        return registry;
    }

}
