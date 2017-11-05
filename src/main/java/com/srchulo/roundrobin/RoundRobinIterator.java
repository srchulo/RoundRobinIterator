package com.srchulo.roundrobin;

import java.util.Collection;
import java.util.Iterator;

/**
 * An {@link Iterator} that returns values in Round-Robin order. Items can be added and removed in O(1) constant time.
 */
public interface RoundRobinIterator<T> extends Iterator<T> {
    /**
     * Returns a {@link RoundRobinIterator} with no initial values.
     */
    static <T> RoundRobinIterator<T> newInstance() {
        return new RoundRobinIteratorImpl<>();
    }

    /**
     * Returns a {@link RoundRobinIterator} with the initial values in values.
     */
    static <T> RoundRobinIterator<T> newInstance(Collection<T> values) {
        return new RoundRobinIteratorImpl<>(values);
    }

    /**
     * Adds a value to the iterator. The value will be placed after the last value retrieved from {@link #next()}, and
     * thus will be returned the next time {@link #next()} is called. This operation is performed in O(1) constant time.
     */
    void add(T value);

    /**
     * Removes value from the {@link Iterator}. This operation is performed in O(1) constant time. It is safe to call
     * this method while iterating. If this value would have been returned by {@link #next()}, the value after it will
     * now be returned.
     */
    void remove(T value);

    /**
     * Returns the number of elements in {@link RoundRobinIterator}.
     */
    int size();

    /**
     * Returns {@code true} if {@link RoundRobinIterator} is empty.
     */
    boolean isEmpty();
}
