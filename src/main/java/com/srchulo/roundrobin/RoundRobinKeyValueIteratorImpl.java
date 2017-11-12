package com.srchulo.roundrobin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.util.Pair;
import javax.annotation.Nullable;

final class RoundRobinKeyValueIteratorImpl<K, V> implements RoundRobinKeyValueIterator<K, V> {
    private final DoublyLinkedList<K, V> doublyLinkedList = new DoublyLinkedList<>();
    private final Map<K, DoublyLinkedList<K, V>.Node> keyToNode = new HashMap<>();

    @Nullable private DoublyLinkedList<K, V>.Node lastNode;
    private boolean canCallRemove;

    RoundRobinKeyValueIteratorImpl() {
        this(ImmutableList.of());
    }

    RoundRobinKeyValueIteratorImpl(Collection<Pair<K, V>> pairs) {
        Preconditions.checkNotNull(pairs).forEach(pair -> add(pair.getKey(), pair.getValue()));
    }

    @Override
    public void add(K key, V value) {
        Preconditions.checkState(
                !containsKey(key), "key '" + key + "' already exists. Cannot add value '" + value + "'");

        DoublyLinkedList<K, V>.Node node =
                lastNode == null ? doublyLinkedList.add(key, value) : doublyLinkedList.addAfter(key, value, lastNode);
        keyToNode.put(key, node);
    }

    @Override
    public V remove(K key) {
        return remove(key, /* loopIterator= */ null);
    }

    private V remove(K key, @Nullable LoopIterator loopIterator) {
        checkContainsKey(key);

        DoublyLinkedList<K, V>.Node node = keyToNode.remove(key);
        if (size() == 1) {
            lastNode = null;
        } else {
            maybeUpdateLastNodeForRemoval(node);
        }

        if (loopIterator != null) {
            loopIterator.onRemove(node);
        }

        doublyLinkedList.remove(node);
        return node.getValue();

    }

    @Override
    public boolean containsKey(K key) {
        return keyToNode.containsKey(key);
    }

    @Override
    public V get(K key) {
        checkContainsKey(key);
        return keyToNode.get(key).getValue();
    }

    private void checkContainsKey(K key) {
        Preconditions.checkArgument(containsKey(key), "key '" + key + "' does not exist for any node");
    }

    private void maybeUpdateLastNodeForRemoval(DoublyLinkedList<K, V>.Node node) {
        if (node == lastNode) {
            lastNode = doublyLinkedList.getPreviousNodeOrTail(lastNode);
        }
    }

    @Override
    public int size() {
        return doublyLinkedList.size();
    }

    @Override
    public boolean isEmpty() {
        return doublyLinkedList.isEmpty();
    }

    @Override
    public boolean hasNext() {
        return !isEmpty();
    }

    @Override
    public V next() {
        Preconditions.checkState(hasNext(), "hasNext() must be true before calling next()");

        canCallRemove = true;
        return getAndSetNextNode().getValue();
    }

    private DoublyLinkedList<K, V>.Node getAndSetNextNode() {
        DoublyLinkedList<K, V>.Node nextNode =
                lastNode == null ? doublyLinkedList.getHead() : doublyLinkedList.getNextNodeOrHead(lastNode);
        lastNode = nextNode;

        return nextNode;
    }

    @Override
    public void remove() {
        Preconditions.checkState(canCallRemove, "Already called remove once for last call to next");

        canCallRemove = false;
        remove(lastNode.getKey());
    }

    @Override
    public Iterator<V> iterator() {
        return this;
    }

    @Override
    public IterableIterator<V> loopIterator() {
        return isEmpty() ? EmptyIterableIterator.INSTANCE : new LoopIterator(/* updateLastNode= */ true);
    }

    @Override
    public IterableIterator<V> statelessLoopIterator() {
        return isEmpty() ? EmptyIterableIterator.INSTANCE : new LoopIterator(/* updateLastNode= */ false);
    }

    @Override
    public String toString() {
        return "RoundRobinKeyValueIteratorImpl{" +
                "doublyLinkedList=" + doublyLinkedList +
                ", keyToNode=" + keyToNode +
                ", lastNode=" + lastNode +
                ", canCallRemove=" + canCallRemove +
                '}';
    }

    private final class LoopIterator implements IterableIterator<V> {
        private final boolean updateLastNode;

        private DoublyLinkedList<K, V>.Node lastLoopNode;
        private DoublyLinkedList<K, V>.Node startNode;
        private boolean canCallRemove;
        private boolean loopFinished;

        private LoopIterator(boolean updateLastNode) {
            this.updateLastNode = updateLastNode;
            lastLoopNode = startNode = lastNode == null ? doublyLinkedList.getTail() : lastNode;
        }

        @Override
        public boolean hasNext() {
            return !loopFinished && !isEmpty();
        }

        @Override
        public V next() {
            Preconditions.checkState(hasNext(), "hasNext() must be true before calling next()");

            lastLoopNode = doublyLinkedList.getNextNodeOrHead(lastLoopNode);
            if (lastLoopNode == startNode) {
                loopFinished = true;
            }
            if (updateLastNode) {
                lastNode = lastLoopNode;
            }

            canCallRemove = true;
            return lastLoopNode.getValue();
        }

        @Override
        public void remove() {
            Preconditions.checkState(canCallRemove, "Already called remove once for last call to next");
            canCallRemove = false;

            RoundRobinKeyValueIteratorImpl.this.remove(lastLoopNode.getKey(), /* loopIterator= */ this);
        }

        private void onRemove(DoublyLinkedList<K, V>.Node nodeToRemove) {
            if (size() == 1) {
                lastLoopNode = null;
                loopFinished = true;
            } else if (lastLoopNode == nodeToRemove) {
                lastLoopNode = doublyLinkedList.getPreviousNodeOrTail(lastLoopNode);
            }
        }

        @Override
        public Iterator<V> iterator() {
            return this;
        }

        @Override
        public String toString() {
            return "LoopIterator{" +
                    "updateLastNode=" + updateLastNode +
                    ", lastLoopNode=" + lastLoopNode +
                    ", startNode=" + startNode +
                    ", canCallRemove=" + canCallRemove +
                    ", loopFinished=" + loopFinished +
                    '}';
        }
    }
}
