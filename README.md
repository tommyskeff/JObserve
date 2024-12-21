# JObserve
 JObserve is a replacement for the legacy java observer package, with support for object propertys and property listeners.

### Why?
JObserve has a few common use cases:
- When you want to give plugins or extensions the ability to add new properties to existing objects.
- When you are interested in the state of an object and want to get notified whenever there is any change.

JObserve provides these two points of functionality in a concise and intuituve manner. See below for a simple example.

It is recomended to initialize property "keys" as static variables, and these can be made public in order to allow other parts of the code to access the property. It is difficult to access an property without the original key (requires reflection), so it is recommended to keep keys public where possible.

The concepts of `Observable` and `PropertyHolder` are considered seperate, however property holder objects are considered observable by default, and you can use the `PropertyRegistry.UPDATE_ATTRIBUTE_OBSERVER` key to observe property changes. There are convinience methods in `PropertyObservable` objects (`observeProperty`) to do this.

Subscriptions can be cancelled at any time with `subscription.cancel()`, and you are given the subscription object after observing an object.

It is **worth noting** that observing an object with a **does not** prevent the object from being garbage collected, however it **does** prevent the callback context from being garbage collected for the lifespan of the observed object, if subscribed with a **strong reference**. Read the JavaDoc in Observable for more information.

### Dependency

We are currently on version `0.4.1`.

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
        <version>0.4.1</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

### Usage
```java
PropertyKey<String> nameProperty = PropertyKey.register("test:name", String.class);
PropertyKey<Integer> balanceProperty = PropertyKey.register("test:balance", Integer.class);

Person person = new Person();
person.setProperty(nameProperty, "John Smith");
person.setProperty(balanceProperty, 1284);

person.observe(balanceProperty, newBalance -> {
    System.out.printf("The balance of %s has changed to %s%n", person.getProperty(nameProperty), newBalance);
});

person.setProperty(balanceProperty, 1500);
person.getPropertyAndUpdate(balanceProperty, balance -> balance == null ? 0 : balance + 50);

/*  OUTPUT
    The balance of John Smith has changed to 1500
    The balance of John Smith has changed to 1550
 */
```