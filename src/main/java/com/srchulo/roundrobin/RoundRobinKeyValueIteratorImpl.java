package com.srchulo.roundrobin;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.HashMap;
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
                !keyToNode.containsKey(key), "key '" + key + "' already exists. Cannot add value '" + value + "'");

        DoublyLinkedList<K, V>.Node node =
                lastNode == null ? doublyLinkedList.add(key, value) : doublyLinkedList.addAfter(key, value, lastNode);
        keyToNode.put(key, node);
    }

    @Override
    public void remove(K key) {
        Preconditions.checkArgument(keyToNode.containsKey(key), "key '" + key + "' does not exist for any node");

        DoublyLinkedList<K, V>.Node node = keyToNode.remove(key);
        maybeUpdateLastNodeForRemoval(node);
        doublyLinkedList.remove(node);
    }

    private void maybeUpdateLastNodeForRemoval(DoublyLinkedList<K, V>.Node node) {
        if (node == lastNode) {
            lastNode = doublyLinkedList.getPreviousNode(lastNode);
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

        lastNode = lastNode == null ? doublyLinkedList.getFirst() : doublyLinkedList.getNextNodeOrFirst(lastNode);
        canCallRemove = true;
        return lastNode.getValue();
    }

    @Override
    public void remove() {
        Preconditions.checkState(canCallRemove, "Already called remove once for last call to next");

        canCallRemove = false;
        remove(lastNode.getKey());
    }

    @Override
    public String toString() {
        return "RoundRobinKeyValueIteratorImpl{" +
                "doublyLinkedList=" + doublyLinkedList +
                ", keyToNode=" + keyToNode +
                ", lastNode=" + lastNode +
                '}';
    }
}
