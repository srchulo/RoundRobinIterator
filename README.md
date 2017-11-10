# RoundRobinIterator
A Round-Robin Iterator that traverses values in Round-Robin order, and can add and remove values in constant time.

<p><a href="http://srchulo.com/java/roundrobin/">Javadoc</a></p>

## RoundRobinIterator

### Maven

    <dependency>
        <groupId>com.srchulo.roundrobin</groupId>
        <artifactId>RoundRobinIterator</artifactId>
        <version>1.2</version>
    </dependency>

### Sample usage

```java

RoundRobinIterator<String> roundRobinIterator = 
    RoundRobinIterator.newInstance(ImmutableList.of("a", "b", "c"));

for (String letter : roundRobinIterator) {
    System.out.println(letter);
}

```

This prints

a

b

c

a

b

c

a

b

c

.

.

.

## RoundRobinKeyValueIterator

```java
RoundRobinKeyValueIterator<Integer, String> roundRobinKeyValueIterator = 
    RoundRobinKeyValueIterator
        .newInstance(
            ImmutableList.of(
                new Pair(1, "a"), 
                new Pair(2, "b"), 
                new Pair(3, "c")));

boolean removedA;
for (String letter : roundRobinKeyValueIterator) {
    System.out.println(letter);
    
    if (!removedA) {
        roundRobinKeyValueIterator.remove(/* key= */ 1);
        removedA = true;
    }
}

```

This prints

a

b

c

b

c

b

c

.

.

.

Elements can also be removed by calling `remove()` after calling `next()` like any other iterator. The above example was 
just to demonstrate how values can be removed by their keys.
