package com.srchulo.roundrobin;

import java.util.Collection;
import java.util.Iterator;
import javafx.util.Pair;

/**
 * An {@link Iterator} that returns values associated with keys in Round-Robin order. Items can be added and removed in
 * O(1) constant time. This {@link Iterator} will loop continuously.
 */
public interface RoundRobinKeyValueIterator<K, V> extends Iterator<V> {
    /**
     * Returns a {@link RoundRobinKeyValueIterator} with no initial values.
     */
    static <K, V> RoundRobinKeyValueIterator<K, V> newInstance() {
        return new RoundRobinKeyValueIteratorImpl<>();
    }

    /**
     * Returns a {@link RoundRobinKeyValueIterator} with the initial key-value pairings in pairs.
     */
    static <K, V> RoundRobinKeyValueIterator<K, V> newInstance(Collection<Pair<K, V>> pairs) {
        return new RoundRobinKeyValueIteratorImpl<>(pairs);
    }

    /**
     * Adds this key-value pair to the iterator. The key-value pair will be placed after the last value retrieved from
     * {@link #next()}, and thus will be returned the next time {@link #next()} is called. This operation is performed
     * in O(1) constant time. If no values have been added, this value is first.
     */
    void add(K key, V value);

    /**
     * Removes the value associated with key from the {@link Iterator}. This operation is performed in O(1) constant
     * time. It is safe to call this method while iterating. If this value would have been returned by {@link #next()}
     * on the next call, the value after it will now be returned.
     */
    void remove(K key);

    /**
     * Returns the number of elements in {@link RoundRobinKeyValueIterator}.
     */
    int size();

    /**
     * Returns {@code true} if {@link RoundRobinKeyValueIterator} is empty.
     */
    boolean isEmpty();
}
