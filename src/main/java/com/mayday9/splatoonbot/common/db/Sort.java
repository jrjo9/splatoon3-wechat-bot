package com.mayday9.splatoonbot.common.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//排序定义
public class Sort {
    private List<SortItem> sortByProperties = new ArrayList<>();

    // 隐藏构造函数
    private Sort() {
    }

    public static Sort byAsc(String property) {
        return new Sort().thenByAsc(property);
    }

    public static Sort byDesc(String property) {
        return new Sort().thenByDesc(property);
    }

    public Sort thenByAsc(String property) {
        sortByProperties.add(new SortItem(property, Direction.ASC));

        return this;
    }

    public Sort thenByDesc(String property) {
        sortByProperties.add(new SortItem(property, Direction.DESC));
        return this;
    }


    public List<SortItem> getSortList() {
        //复制、只读
        List<SortItem> copyList = new ArrayList<>(sortByProperties);
        return Collections.unmodifiableList(copyList);
    }

    public class SortItem {
        private String property;
        private Direction direction;

        private SortItem(String property, Direction direction) {
            this.property = property;
            this.direction = direction;
        }

        public String getProperty() {
            return property;
        }

        public Direction getDirection() {
            return direction;
        }
    }

    public enum Direction {
        ASC, DESC;
    }
}
