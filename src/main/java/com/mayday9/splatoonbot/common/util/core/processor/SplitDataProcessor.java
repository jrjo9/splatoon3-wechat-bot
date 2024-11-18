package com.mayday9.splatoonbot.common.util.core.processor;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public final class SplitDataProcessor {

    public static <T> List<List<T>> splitListLimitSize(List<T> conditions, int size) {
        List<List<T>> multiConditions = new ArrayList<>();
        int count = conditions.size() / size;

        for (int j = 0; j <= count; j++) {
            final List<T> subList;

            int offset = j * size;

            if (offset + size >= conditions.size()) {
                subList = conditions.subList(offset, conditions.size());
            } else {
                subList = conditions.subList(offset, offset + size);
            }
            if (!CollectionUtils.isEmpty(subList)) {
                multiConditions.add(subList);
            }
        }
        return multiConditions;
    }
}
