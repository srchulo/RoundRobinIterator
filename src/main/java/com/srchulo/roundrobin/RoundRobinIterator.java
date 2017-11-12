package com.srchulo.roundrobin;

import java.util.Collection;
import java.util.Iterator;

/**
 * An {@link Iterator} that returns values in Round-Robin order. Items can be added and removed in O(1) constant time.
 * This {@link Iterator} will loop continuously.
 */
public interface RoundRobinIterator<T> extends IterableIterator<T> {
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
     * If no values have been added, this value is first.
     */
    void add(T value);

    /**
     * Removes value from the {@link Iterator}. This operation is performed in O(1) constant time. It is safe to call
     * this method while iterating. If this value would have been returned by {@link #next()} on the next call, the
     * value after it will now be returned.
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

    /**
     * Starts a loop where you can do one iteration over each element in the iterator starting from the last
     * element returned by {@link #next()}. The iteration over this loop does affect the {@link Iterator}'s position
     * once the loop ends, meaning the element returned by {@link #next()} may be different from when you started if you
     * exit the loop early.
     *
     * <p> This {@link IterableIterator} is a child of the {@link RoundRobinIterator} that created it, meaning that any
     * changes on it will affect its parent (such as calling {@link #remove()}. However, the parent
     * {@link RoundRobinIterator} should not be used while this {@link IterableIterator} is in use as it may break it.
     */
    IterableIterator<T> loopIterator();

    /**
     * Starts a loop where you can do one iteration over each element in the iterator starting from the last
     * element returned by {@link #next()}. The iteration over this loop does not affect the {@link Iterator}'s position
     * once the loop ends, meaning that the element returned by {@link #next()} will be the same as what would've been
     * returned before the loop started (unless that element was removed).
     *
     * <p> This {@link IterableIterator} is a child of the {@link RoundRobinIterator} that created it, meaning that any
     * changes on it will affect its parent (such as calling {@link #remove()}. However, the parent
     * {@link RoundRobinIterator} should not be used while this {@link IterableIterator} is in use as it may break it.
     */
    IterableIterator<T> statelessLoopIterator();
}
