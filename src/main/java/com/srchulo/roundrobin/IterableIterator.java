package com.srchulo.roundrobin;

import java.util.Iterator;

/** Interface that implements both {@link Iterable} and {@link Iterator}. */
public interface IterableIterator<T> extends Iterable<T>, Iterator<T> { }
