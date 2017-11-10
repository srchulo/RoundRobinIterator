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
    @Nullable private DoublyLinkedList<K, V>.Node loopNode;
    private boolean loopStarted;
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
        checkContainsKey(key);

        DoublyLinkedList<K, V>.Node node = keyToNode.remove(key);
        maybeUpdateLastNodeForRemoval(node);
        maybeUpdateLoopNodeForRemoval(node);
        doublyLinkedList.remove(node);

        if (isEmpty()) {
            loopNode = null;
        }

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
            lastNode = doublyLinkedList.getPreviousNode(lastNode);
        }
    }

    private void maybeUpdateLoopNodeForRemoval(DoublyLinkedList<K, V>.Node node) {
        if (node == loopNode) {
            loopNode = doublyLinkedList.getPreviousNodeOrTail(loopNode);
            maybeClearLoopNode();
        }
    }

    @Override
    public void startLoop() {
        Preconditions
                .checkState(!loopStarted, "current loop must be ended by calling endLoop before calling startLoop");
        loopStarted = true;

        if (isEmpty()) {
            return;
        }

        loopNode = lastNode == null ? doublyLinkedList.getTail() : lastNode;
    }

    @Override
    public boolean inLoop() {
        Preconditions.checkState(loopStarted, "startLoop must be called before calling inLoop");

        if (loopNode == null) {
            loopStarted = false;
            return false;
        }

        return true;
    }

    @Override
    public void endLoop() {
        Preconditions.checkState(loopStarted, "startLoop must be called before calling endLoop");
        loopStarted = false;
        loopNode = null;
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

        lastNode = getNextNode();
        maybeClearLoopNode();
        canCallRemove = true;
        return lastNode.getValue();
    }

    private DoublyLinkedList<K, V>.Node getNextNode() {
        return lastNode == null ? doublyLinkedList.getHead() : doublyLinkedList.getNextNodeOrHead(lastNode);
    }

    private void maybeClearLoopNode() {
        // We do not call endLoop because we don't want loopStarted to be false. This way the user should be able to
        // call inLoop one more time.
        if (lastNode == loopNode) {
            loopNode = null;
        }
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
