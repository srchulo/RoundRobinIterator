package com.srchulo.roundrobin;

import java.util.Collection;
import java.util.Iterator;
import javafx.util.Pair;

/**
 * An {@link Iterator} that returns values associated with keys in Round-Robin order. Items can be added and removed in
 * O(1) constant time. This {@link Iterator} will loop continuously.
 */
public interface RoundRobinKeyValueIterator<K, V> extends IterableIterator<V> {
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
     * @return the value associated with key.
     */
    V remove(K key);

    /** Returns {@code true} if this {@link RoundRobinKeyValueIterator} contains a value for this key. */
    boolean containsKey(K key);

    /**
     * Returns the value associated with this key.
     * @throws IllegalArgumentException if this key does not exist.
     */
    V get(K key);

    /**
     * Returns the number of elements in {@link RoundRobinKeyValueIterator}.
     */
    int size();

    /**
     * Returns {@code true} if {@link RoundRobinKeyValueIterator} is empty.
     */
    boolean isEmpty();

    /**
     * Starts a loop where you can do one iteration over each element in the iterator starting from the last
     * element returned by {@link #next()}. The iteration over this loop does affect the {@link Iterator}'s position
     * once the loop ends, meaning the element returned by {@link #next()} may be different from when you started if you
     * exit the loop early.
     *
     * <p> This {@link IterableIterator} is a child of the {@link RoundRobinKeyValueIterator} that created it, meaning
     * that any changes on it will affect its parent (such as calling {@link #remove()}. However, the parent
     * {@link RoundRobinKeyValueIterator} should not be used while this {@link IterableIterator} is in use as it may
     * break it.
     */
    IterableIterator<V> loopIterator();

    /**
     * Starts a loop where you can do one iteration over each element in the iterator starting from the last
     * element returned by {@link #next()}. The iteration over this loop does not affect the {@link Iterator}'s position
     * once the loop ends, meaning that the element returned by {@link #next()} will be the same as what would've been
     * returned before the loop started (unless that element was removed).
     *
     * <p> This {@link IterableIterator} is a child of the {@link RoundRobinKeyValueIterator} that created it, meaning
     * that any changes on it will affect its parent (such as calling {@link #remove()}. However, the parent
     * {@link RoundRobinKeyValueIterator} should not be used while this {@link IterableIterator} is in use as it may
     * break it.
     */
    IterableIterator<V> statelessLoopIterator();
}
