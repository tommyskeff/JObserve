# JObserve
 JObserve is a replacement for the legacy java observer package, with support for object attributes and attribute listeners.

### Why?
JObserve has a few common use cases:
- When you want to give plugins or extensions the ability to add new properties to existing objects.
- When you are interested in the state of an object and want to get notified whenever there is any change.

JObserve provides these two points of functionality in a concise and intuituve manner. See below for a simple example.

It is recomended to initialize attribute "keys" as static variables, and these can be made public in order to allow other parts of the code to access the attribute. It is difficult to access an attribute without the original key (requires reflection), so it is recommended to keep keys public where possible.

The concepts of `Observable` and `AttributeHolder` are considered seperate, however attribute holder objects are considered observable by default, and you can use the `AttributeRegistry.UPDATE_ATTRIBUTE_OBSERVER` key to observe attribute changes. There are convinience methods in `AttributeObservable` objects (`observeAttribute`) to do this.

Subscriptions can be cancelled at any time with `subscription.cancel()`, and you are given the subscription object after observing an object.

It is **worth noting** that observing an object **does not** prevent the object from being garbage collected, however it **does** prevent the callback context from being garbage collected for the lifespan of the observed object.

### Dependency

We are currently on version `0.2.0`.

```xml
<repositories>
    <repository>
        <id>tommyjs-repo</id>
        <url>https://repo.tommyjs.dev/repository/maven-releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.tommyjs</groupId>
        <artifactId>JObserve</artifactId>
        <version>0.2.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

### Usage
```java
AttributeKey<String> nameAttribute = AttributeKey.register(String.class);
AttributeKey<Integer> balanceAttribute = AttributeKey.register(Integer.class);

Person person = new Person();
person.setAttribute(nameAttribute, "John Smith");
person.setAttribute(balanceAttribute, 1284);

person.observeAttribute(balanceAttribute, newBalance -> {
    System.out.printf("The balance of %s has changed to %s%n", person.getAttribute(nameAttribute), newBalance);
});

person.setAttribute(balanceAttribute, 1500);
person.getAndUpdateAttribute(balanceAttribute, balance -> balance == null ? 0 : balance + 50);

/*  OUTPUT
    The balance of John Smith has changed to 1500
    The balance of John Smith has changed to 1550
 */
```