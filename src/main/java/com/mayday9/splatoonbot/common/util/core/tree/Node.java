package com.mayday9.splatoonbot.common.util.core.tree;

import org.springframework.util.CollectionUtils;

import java.util.Set;

public class Node<T> {

    public static <T> Node<T> node(T t) {
        Node<T> node = new Node<>();
        node.setValue(t);
        return node;
    }

    private Node<T> parent;

    private T value;

    private Set<Node<T>> children;

    public boolean isRoot() {
        return null == this.parent;
    }

    public boolean hasChildren() {
        return CollectionUtils.isEmpty(children);
    }

    public int numberOfChildren() {
        return hasChildren() ? children.size() : 0;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Set<Node<T>> getChildren() {
        return children;
    }

    public void setChildren(Set<Node<T>> children) {
        this.children = children;
        for (Node<T> child : children) {
            child.setParent(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node<?> node = (Node<?>) o;

        return value != null ? value.equals(node.value) : node.value == null;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * (value != null ? value.hashCode() : 0)) + (value != null ? value.hashCode() : 0);
    }
}
