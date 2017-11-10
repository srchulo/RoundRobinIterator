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
     * Starts a loop where you can do one iteration over each element in the iterator starting from the last
     * element returned by {@link #next()}. You can check to see if you are in the loop by calling {@link #inLoop()}.
     *
     * <pre>
     * <code>
     *     roundRobinKeyValueIterator.startLoop();
     *     while (roundRobinKeyValueIterator.hasNext() && roundRobinKeyValueIterator.inLoop()) {
     *         System.out.println("In loop " + roundRobinKeyValueIterator.next());
     *     }
     * </code>
     * </pre>
     *
     * If any elements are removed from the {@link Iterator} while in a loop, the loop will still stop at the correct
     * place, even if the element removed is the element where the loop started. If this is the case, the element will
     * be moved one element back. If all elements are removed and the list is empty, the loop will be stopped and
     * {@link #inLoop()} will return {@code false}.
     */
    void startLoop();

    /**
     * Returns {@code true} while still in the loop started by {@link #startLoop()}.
     */
    boolean inLoop();

    /**
     * Ends the current loop initiated by calling {@link #startLoop()}.
     */
    void endLoop();

    /**
     * Returns the number of elements in {@link RoundRobinKeyValueIterator}.
     */
    int size();

    /**
     * Returns {@code true} if {@link RoundRobinKeyValueIterator} is empty.
     */
    boolean isEmpty();
}
