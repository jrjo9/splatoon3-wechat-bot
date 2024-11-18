package com.mayday9.splatoonbot.common.util.core.tree;

import com.mayday9.splatoonbot.common.util.core.AssertUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class TreeBuilder<T> {

    private final Set<Node<T>> roots = new HashSet<>();
    private final Map<Node<T>, Set<Node<T>>> childrenMap = Maps.newHashMap();
    private final Peek<Node<T>> peek;

    private TreeBuilder(Peek<Node<T>> peek) {
        this.peek = peek;
    }

    public static <T> TreeBuilder<T> builder(Peek<Node<T>> peek) {
        return new TreeBuilder<>(peek);
    }

    public TreeBuilder<T> recursion(Node<T> value) {
        Node<T> parent = peek.peek(value);
        if (parent == null) {
            roots.add(value);
        } else {
            if (!childrenMap.containsKey(parent)) {
                childrenMap.put(parent, new HashSet<Node<T>>());
            }
            childrenMap.get(parent).add(value);
            recursion(parent);
        }
        return this;
    }

    public List<Node<T>> build() {
        AssertUtils.assertTrue(roots.isEmpty(), "根节点不能为空。");
        set(roots);
        return Lists.newArrayList(this.roots);
    }

    private void set(Set<Node<T>> parents) {
        for (Node<T> parent : parents) {
            if (childrenMap.containsKey(parent)) {
                Set<Node<T>> nodes = childrenMap.get(parent);
                parent.setChildren(Collections.unmodifiableSet(nodes));
                set(nodes);
            }
        }
    }
}
