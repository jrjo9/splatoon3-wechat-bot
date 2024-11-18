package com.mayday9.splatoonbot.common.util.core;

public interface FieldSelector<Type, FieldType> {
    FieldType select(Type type);
}
