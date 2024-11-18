package com.mayday9.splatoonbot.common.util.core.tree;

import java.util.Map;

public class ParentPeek<T> implements Peek<Node<T>> {

    private final Map<Node<T>, Node<T>> childParentMap;

    public ParentPeek(Map<Node<T>, Node<T>> childParentMap) {
        this.childParentMap = childParentMap;
    }

    @Override
    public Node<T> peek(Node<T> value) {
        return childParentMap.get(value);
    }
}
