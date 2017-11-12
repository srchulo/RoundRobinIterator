package com.srchulo.roundrobin;

import java.util.Collection;
import java.util.Iterator;

/**
 * An {@link Iterator} that returns values in Round-Robin order. Items can be added and removed in O(1) constant time.
 * This {@link Iterator} will loop continuously.
 */
public interface RoundRobinIterator<T> extends Iterator<T>, Iterable<T> {
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
     * @throws IllegalStateException if called while in a loop started by {@link #startLoop()}.
     */
    void add(T value);

    /**
     * Removes value from the {@link Iterator}. This operation is performed in O(1) constant time. It is safe to call
     * this method while iterating. If this value would have been returned by {@link #next()} on the next call, the
     * value after it will now be returned.
     * @throws IllegalStateException if called while in a loop started by {@link #startLoop()}.
     */
    void remove(T value);

    /**
     * Starts a loop where you can do one iteration over each element in the iterator starting from the last
     * element returned by {@link #next()}. The iteration over this loop does not affect the {@link Iterator}'s position
     * once the loop ends. {@link #hasNext()} will return {@code false} when the loop is ended.
     *
     * <pre>
     * <code>
     *     roundRobinIterator.startLoop();
     *
     *     // we could also use a foreach loop.
     *     while (roundRobinIterator.hasNextAndInLoop()) {
     *         System.out.println("In loop " + roundRobinKeyValueIterator.next());
     *
     *         // if condition is never true, the loop will exit after a full loop when inLoop() returns false.
     *         if (condition == true) {
     *             roundRobinIterator.endLoop();
     *         }
     *     }
     *
     *     // next value roundRobinIterator would've returned before starting the loop.
     *     System.out.println("Out of loop " + roundRobinIterator.next());
     * </code>
     * </pre>
     *
     * If any elements are removed from the {@link Iterator} while in a loop, the loop will still stop at the correct
     * place, even if the element removed is the element where the loop started. If this is the case, the element will
     * be moved one element back. If all elements are removed and the list is empty, the loop will be stopped and
     * {@link #hasNext()} ()} will return {@code false}.
     */
    void startLoop();

    /**
     * Ends the current loop initiated by calling {@link #startLoop()}.
     */
    void endLoop();

    /**
     * Returns the number of elements in {@link RoundRobinIterator}.
     */
    int size();

    /**
     * Returns {@code true} if {@link RoundRobinIterator} is empty.
     */
    boolean isEmpty();
}
