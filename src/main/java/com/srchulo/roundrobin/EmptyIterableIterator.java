package com.srchulo.roundrobin;

import java.util.Iterator;

final class EmptyIterableIterator<T> implements IterableIterator<T> {
    static final EmptyIterableIterator INSTANCE = new EmptyIterableIterator();

    private EmptyIterableIterator() { }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "EmptyIterableIterator{}";
    }
}
