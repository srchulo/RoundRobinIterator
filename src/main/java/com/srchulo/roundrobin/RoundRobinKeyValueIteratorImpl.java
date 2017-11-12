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
    @Nullable private DoublyLinkedList<K, V>.Node loopNode;
    private LoopState loopState = LoopState.NOT_STARTED;
    private boolean canCallRemove;

    private enum LoopState {
        NOT_STARTED,
        STARTED,
        ENDED,
    }

    RoundRobinKeyValueIteratorImpl() {
        this(ImmutableList.of());
    }

    RoundRobinKeyValueIteratorImpl(Collection<Pair<K, V>> pairs) {
        Preconditions.checkNotNull(pairs).forEach(pair -> add(pair.getKey(), pair.getValue()));
    }

    @Override
    public void add(K key, V value) {
        Preconditions.checkState(loopState != LoopState.STARTED, "cannot call add while in a loop");
        Preconditions.checkState(
                !containsKey(key), "key '" + key + "' already exists. Cannot add value '" + value + "'");

        DoublyLinkedList<K, V>.Node node =
                lastNode == null ? doublyLinkedList.add(key, value) : doublyLinkedList.addAfter(key, value, lastNode);
        keyToNode.put(key, node);
    }

    @Override
    public V remove(K key) {
        Preconditions.checkState(loopState != LoopState.STARTED, "cannot call remove(key) while in a loop");
        return removeWithoutLoopStateCheck(key);
    }

    private V removeWithoutLoopStateCheck(K key) {
        checkContainsKey(key);

        DoublyLinkedList<K, V>.Node node = keyToNode.remove(key);
        if (size() == 1) {
            lastNode = null;
            loopNode = null;
        } else {
            maybeUpdateLastNodeForRemoval(node);
            maybeUpdateLoopNodeForRemoval(node);
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

    private void maybeUpdateLoopNodeForRemoval(DoublyLinkedList<K, V>.Node node) {
        if (node == loopNode) {
            loopNode = doublyLinkedList.getPreviousNodeOrTail(loopNode);
        }
    }

    @Override
    public void startLoop() {
        Preconditions
                .checkState(
                        loopState == LoopState.NOT_STARTED || loopState == LoopState.ENDED,
                        "current loop must be ended by calling endLoop before calling startLoop");
        loopState = LoopState.STARTED;

        if (isEmpty()) {
            return;
        }

        // if lastNode is not set, the loop will never end
        if (lastNode == null) {
            lastNode = doublyLinkedList.getTail();
        }
        loopNode = lastNode;
    }

    private boolean inLoop() {
        return loopNode != null;
    }

    @Override
    public void endLoop() {
        Preconditions.checkState(loopState == LoopState.STARTED, "startLoop must be called before calling endLoop");
        loopState = LoopState.ENDED;
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
        if (loopState == LoopState.NOT_STARTED) {
            return !isEmpty();
        }

        if (loopNode == null) {
            loopState = LoopState.NOT_STARTED;
            return false;
        }

        // this should always be true
        return !isEmpty();
    }

    @Override
    public V next() {
        Preconditions.checkState(hasNext(), "hasNext() must be true before calling next()");

        DoublyLinkedList<K, V>.Node nextNode = getAndSetNextNode();
        maybeClearLoopNode();
        canCallRemove = true;
        return nextNode.getValue();
    }

    private DoublyLinkedList<K, V>.Node getAndSetNextNode() {
        DoublyLinkedList<K, V>.Node nextNode;
        if (inLoop()) {
            nextNode = doublyLinkedList.getNextNodeOrHead(loopNode);
            loopNode = nextNode;
        } else {
            nextNode = lastNode == null ? doublyLinkedList.getHead() : doublyLinkedList.getNextNodeOrHead(lastNode);
            lastNode = nextNode;
        }

        return nextNode;
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
        removeWithoutLoopStateCheck(inLoop() ? loopNode.getKey() : lastNode.getKey());
    }

    @Override
    public Iterator<V> iterator() {
        return this;
    }

    @Override
    public String toString() {
        return "RoundRobinKeyValueIteratorImpl{" +
                "doublyLinkedList=" + doublyLinkedList +
                ", keyToNode=" + keyToNode +
                ", lastNode=" + lastNode +
                ", loopNode=" + loopNode +
                ", loopState=" + loopState +
                ", canCallRemove=" + canCallRemove +
                '}';
    }
}
