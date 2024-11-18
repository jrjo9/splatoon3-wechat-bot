package com.mayday9.splatoonbot.common.util.core.processor;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class DataSortAndRankProcessor {

    /**
     * 对集合元素对进排序并设置排名
     *
     * @param elements         待处理集合
     * @param compareFieldName 排序比较字段
     * @param sortType         排序方式 1:升序,-1,降序
     * @param rankFieldName    排名字段
     * @param <E>              集合元素
     */
    public static <E> void sortAndSetRank(final List<E> elements, final String compareFieldName, final int sortType, final String rankFieldName) {
        if (CollectionUtils.isEmpty(elements)) {
            return;
        }
        sort(elements, compareFieldName, sortType);
        setRank(elements, compareFieldName, rankFieldName);
    }

    @SuppressWarnings("PMD")
    public static <C extends Comparable, E> void sort(final List<E> elements, final String compareFieldName, final int sortType) {
        Collections.sort(elements, new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                try {
                    Field compareField = ReflectionUtils.findField(o1.getClass(), compareFieldName);
                    compareField.setAccessible(true);
                    C compareField1Value = (C) compareField.get(o1);
                    C compareField2Value = (C) compareField.get(o2);
                    if (compareField1Value == compareField2Value) {
                        return 0;
                    }
                    if (compareField1Value == null) {
                        return 1;
                    }
                    if (compareField2Value == null) {
                        return -1;
                    }
                    return sortType * compareField1Value.compareTo(compareField2Value);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        });
    }

    public static <C extends Comparable, E> void setRank(final List<E> elements, final String compareFieldName, final String rankFieldName) {
        try {
            E firstElement = elements.get(0);
            Field compareField = ReflectionUtils.findField(firstElement.getClass(), compareFieldName);
            compareField.setAccessible(true);
            C tempCompareFieldValue = (C) compareField.get(firstElement);
            if (tempCompareFieldValue == null) {
                return;
            }
            Field rankField = ReflectionUtils.findField(firstElement.getClass(), rankFieldName);
            int rankStart = 1;
            rankField.setAccessible(true);
            for (E value : elements) {
                C c = (C) compareField.get(value);
                if (c == null) {
                    break;
                }
                if (c.compareTo(tempCompareFieldValue) != 0) {
                    rankStart++;
                }
                rankField.set(value, rankStart);
                tempCompareFieldValue = c;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
