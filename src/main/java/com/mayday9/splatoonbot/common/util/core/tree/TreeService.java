package com.mayday9.splatoonbot.common.util.core.tree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

public enum TreeService {
    INSTANCE;

    public <E extends TreeNode> E convertTree(List<E> elementList) {
        if (CollectionUtils.isEmpty(elementList)) return null;

        Map<Integer, List<E>> treeNodeMap = Maps.newTreeMap();
        for (E element : elementList) {
            if (treeNodeMap.containsKey(element.getParentId())) {
                treeNodeMap.get(element.getParentId()).add(element);
            } else {
                treeNodeMap.put(element.getParentId(), Lists.newArrayList(element));
            }
        }

        List<E> firstLevelNodes = treeNodeMap.get(0);
        if (CollectionUtils.isEmpty(firstLevelNodes)) return null;
        E rootNode = (E) new TreeNode();
        rootNode.setLevel(0);
        rootNode.getChildNodes().addAll(firstLevelNodes);
        setTreeNodeLevel(treeNodeMap, firstLevelNodes, rootNode.getLevel());
        return rootNode;
    }

    private <E extends TreeNode> void setTreeNodeLevel(Map<Integer, List<E>> treeNodeMap, List<E> parentNodes, int parentLevel) {
        if (CollectionUtils.isEmpty(parentNodes)) return;

        for (E element : parentNodes) {
            element.setLevel(parentLevel + 1);
            List<E> childNodes = treeNodeMap.get(element.getNodeId());
            if (!CollectionUtils.isEmpty(childNodes)) {
                element.getChildNodes().addAll(childNodes);
                setTreeNodeLevel(treeNodeMap, childNodes, element.getLevel());
            }
        }
    }

}
