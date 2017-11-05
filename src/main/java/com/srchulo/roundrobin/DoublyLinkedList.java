package com.srchulo.roundrobin;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

/**
 * A doubly linked list implementation.
 * Based on DoublyLinkedList by William Fiset, william.alexandre.fiset@gmail.com
 * Modified from https://github.com/williamfiset/data-structures
 **/
final class DoublyLinkedList<K, V> {
    private int size = 0;
    private Node head = null;
    private Node tail = null;

    final class Node {
        private K key;
        private V value;
        private Node prev, next;

        private Node(K key, V value, Node prev, Node next) {
            this.key = key;
            this.value = value;
            this.prev = prev;
            this.next = next;
        }

        K getKey() {
            return key;
        }

        V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    int size() {
        return size;
    }

    boolean isEmpty() {
        return size() == 0;
    }

    Node add(K key, V value) {
        if (isEmpty()) {
            head = tail = new Node(key, value, /* prev= */ null, /* next= */ null);
        } else {
            tail = tail.next = new Node(key, value, /* prev= */ tail, /* next= */ null);
        }
        size++;

        return tail;
    }

    Node addAfter(K key, V value, Node node) {
        Preconditions.checkNotNull(node, "node cannot be null");

        if (node == tail) {
            return add(key, value);
        }

        Node next = node.next;
        next.prev = node.next = new Node(key, value, /* prev= */ node, next);

        return node.next;
    }

    @Nullable
    Node getPreviousNode(Node node) {
        return node.prev;
    }

    Node getNextNodeOrFirst(Node node) {
        if (node == tail) {
            return head;
        }

        return node.next;
    }

    Node getFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("DoublyLinkedList is empty");
        }

        return head;
    }

    void remove(Node node) {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot remove from an empty DoublyLinkedList");
        }

        if (node == head) {
            removeFirst();
            return;
        }
        if (node == tail) {
            removeLast();
            return;
        }

        node.next.prev = node.prev;
        node.prev.next = node.next;

        node.key = null;
        node.value = null;
        node.prev = node.next = null;

        size--;
    }

    private void removeFirst() {
        head = head.next;
        size--;

        if (isEmpty()) {
            tail = null;
        } else {
            head.prev = null;
        }
    }

    private void removeLast() {
        tail = tail.prev;
        size--;

        if (isEmpty()) {
            head = null;
        } else {
            tail.next = null;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("[");

        Node current = head;
        while (current != null) {
            stringBuilder.append(current);
            current = current.next;

            if (current != null) {
                stringBuilder.append(", ");
            }
        }

        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}

