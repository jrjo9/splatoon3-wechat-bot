package com.mayday9.splatoonbot.common.util.core.property;

import java.util.List;

public class Setters<T> {

    public static <T> Setters<T> instance() {
        return new Setters<>();
    }

    public ListPropertySetter<T> list(List<T> values) {
        return new ListPropertySetter<>(values);
    }
}
