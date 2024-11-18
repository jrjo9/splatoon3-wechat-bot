package com.mayday9.splatoonbot.common.util.core.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    private int level;

    public int getNodeId() {
        return 0;
    }

    public int getParentId() {
        return 0;
    }

    private final List<TreeNode> childNodes = new ArrayList<>();

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public List<TreeNode> getChildNodes() {
        return childNodes;
    }
}
