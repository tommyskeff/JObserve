package dev.tommyjs.jobserve.attribute;

public abstract class AttributeHolderObject implements AttributeHolder {

    private final AttributeRegistry registry;

    public AttributeHolderObject() {
        this.registry = AttributeRegistry.create();
    }

    @Override
    public AttributeRegistry getAttributes() {
        return registry;
    }

}
