package com.mayday9.splatoonbot.common.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//排序定义
public class Props {

    private List<PropertyItem> properties = new ArrayList<>();

    // 隐藏构造函数
    private Props() {
    }

    public static Props byEq(String propertyName, Object propertyValue) {
        return new Props().thenByEq(propertyName, propertyValue);
    }

    public static Props byLike(String propertyName, Object propertyValue) {
        return new Props().thenByLike(propertyName, propertyValue);
    }

    public static Props byGe(String propertyName, Object propertyValue) {
        return new Props().thenByGe(propertyName, propertyValue);
    }

    public static Props byGt(String propertyName, Object propertyValue) {
        return new Props().thenByGt(propertyName, propertyValue);
    }

    public static Props byLe(String propertyName, Object propertyValue) {
        return new Props().thenByLe(propertyName, propertyValue);
    }

    public static Props byLt(String propertyName, Object propertyValue) {
        return new Props().thenByLt(propertyName, propertyValue);
    }

    public static Props byNe(String propertyName, Object propertyValue) {
        return new Props().thenByNe(propertyName, propertyValue);
    }

    public static Props byIn(String propertyName, Object propertyValue) {
        return new Props().thenByIn(propertyName, propertyValue);
    }

    public static Props byAsc(String propertyName) {
        return new Props().thenByAsc(propertyName);
    }

    public static Props byDesc(String propertyName) {
        return new Props().thenByDesc(propertyName);
    }

    public Props thenByEq(String propertyName, Object propertyValue) {
        properties.add(new PropertyItem(propertyName, propertyValue, Direction.EQ));
        return this;
    }

    public Props thenByLike(String propertyName, Object propertyValue) {
        properties.add(new PropertyItem(propertyName, propertyValue, Direction.LIKE));
        return this;
    }

    public Props thenByGe(String propertyName, Object propertyValue) {
        properties.add(new PropertyItem(propertyName, propertyValue, Direction.GE));
        return this;
    }

    public Props thenByGt(String propertyName, Object propertyValue) {
        properties.add(new PropertyItem(propertyName, propertyValue, Direction.GT));
        return this;
    }

    public Props thenByLe(String propertyName, Object propertyValue) {
        properties.add(new PropertyItem(propertyName, propertyValue, Direction.LE));
        return this;
    }

    public Props thenByLt(String propertyName, Object propertyValue) {
        properties.add(new PropertyItem(propertyName, propertyValue, Direction.LT));
        return this;
    }

    public Props thenByNe(String propertyName, Object propertyValue) {
        properties.add(new PropertyItem(propertyName, propertyValue, Direction.NE));
        return this;
    }

    public Props thenByIn(String propertyName, Object propertyValue) {
        properties.add(new PropertyItem(propertyName, propertyValue, Direction.IN));
        return this;
    }

    public Props thenByAsc(String propertyName) {
        properties.add(new PropertyItem(propertyName, propertyName, Direction.ASC));
        return this;
    }

    public Props thenByDesc(String propertyName) {
        properties.add(new PropertyItem(propertyName, propertyName, Direction.DESC));
        return this;
    }

    public List<PropertyItem> getProperties() {
        //复制、只读
        List<PropertyItem> copyList = new ArrayList<>(properties);
        return Collections.unmodifiableList(copyList);
    }

    public Map<Direction, List<PropertyItem>> getPropertiesMap() {
        if (null != properties && properties.size() > 0) {
            return properties.stream().collect(Collectors.groupingBy(PropertyItem::getDirection));
        }
        return null;
    }

    public class PropertyItem {
        private String propertyName;
        private Object propertyValue;
        private Direction direction;

        private PropertyItem(String property, Object value, Direction direction) {
            this.propertyName = property;
            this.propertyValue = value;
            this.direction = direction;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public Object getPropertyValue() {
            return propertyValue;
        }

        public Direction getDirection() {
            return direction;
        }
    }

    public enum Direction {
        EQ, LIKE, GE, GT, LE, LT, NE, IN,
        ASC, DESC;
    }
}
