package com.srchulo.roundrobin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;

final class RoundRobinIteratorImpl<T> implements RoundRobinIterator<T> {
    private final RoundRobinKeyValueIterator<T, T> roundRobinKeyValueIterator =
            RoundRobinKeyValueIterator.newInstance();

    RoundRobinIteratorImpl() {
        this(ImmutableList.of());
    }

    RoundRobinIteratorImpl(Collection<T> collection) {
        Preconditions.checkNotNull(collection).forEach(this::add);
    }

    @Override
    public void add(T value) {
        roundRobinKeyValueIterator.add(value, value);
    }

    @Override
    public void remove(T value) {
        roundRobinKeyValueIterator.remove(value);
    }

    @Override
    public int size() {
        return roundRobinKeyValueIterator.size();
    }

    @Override
    public boolean isEmpty() {
        return roundRobinKeyValueIterator.isEmpty();
    }

    @Override
    public boolean hasNext() {
        return roundRobinKeyValueIterator.hasNext();
    }

    @Override
    public T next() {
        return roundRobinKeyValueIterator.next();
    }

    @Override
    public void remove() {
        roundRobinKeyValueIterator.remove();
    }

    @Override
    public Iterator<T> iterator() {
        return roundRobinKeyValueIterator.iterator();
    }

    @Override
    public IterableIterator<T> loopIterator() {
        return roundRobinKeyValueIterator.loopIterator();
    }

    @Override
    public IterableIterator<T> statelessLoopIterator() {
        return roundRobinKeyValueIterator.statelessLoopIterator();
    }

    @Override
    public String toString() {
        return "RoundRobinIteratorImpl{" +
                "roundRobinKeyValueIterator=" + roundRobinKeyValueIterator +
                '}';
    }
}
