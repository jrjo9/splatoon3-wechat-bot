package com.mayday9.splatoonbot.common.util.core.tree;

import com.google.common.collect.Lists;

import java.util.List;

public class Tree<T> {

    private final List<Node<T>> nodeList = Lists.newArrayList();

    public List<Node<T>> getNodeList() {
        return nodeList;
    }
}
