package com.mayday9.splatoonbot.common.util.core.property;

import java.util.List;

public class ListPropertySetter<T> {

    private final List<T> values;

    public ListPropertySetter(List<T> values) {
        this.values = values;
    }

    public List<T> cycleSetProperties(PropertySetter<T> setter) {

        if (null == values) return values;

        for (T value : values) {
            setter.apply(value);
        }
        return values;
    }
}
