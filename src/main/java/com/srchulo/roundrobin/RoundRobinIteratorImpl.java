package com.srchulo.roundrobin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;

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
    public void startLoop() {
        roundRobinKeyValueIterator.startLoop();
    }

    @Override
    public boolean inLoop() {
        return roundRobinKeyValueIterator.inLoop();
    }

    @Override
    public void endLoop() {
        roundRobinKeyValueIterator.endLoop();
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
    public String toString() {
        return "RoundRobinIteratorImpl{" +
                "roundRobinKeyValueIterator=" + roundRobinKeyValueIterator +
                '}';
    }
}
