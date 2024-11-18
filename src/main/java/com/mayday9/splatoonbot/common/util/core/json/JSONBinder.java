package com.mayday9.splatoonbot.common.util.core.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

@Deprecated
public final class JSONBinder<T> {
    static final ObjectMapper DEFAULT_OBJECT_MAPPER;

    static {
        DEFAULT_OBJECT_MAPPER = ObjectMapperBuilder.defaultObjectMapper().get();
    }

    public static <T> JSONBinder<T> binder(Class<T> beanClass) {
        return new JSONBinder<>(beanClass);
    }

    public static ObjectMapper getObjectMapper() {
        return DEFAULT_OBJECT_MAPPER;
    }

    ObjectMapper objectMapper;
    private final Class<T> beanClass;

    private JSONBinder(Class<T> beanClass) {
        this.beanClass = beanClass;
        this.objectMapper = DEFAULT_OBJECT_MAPPER;
    }

    public T fromJSON(String json) {
        try {
            return objectMapper.readValue(json, beanClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJSON(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONBinder<T> indentOutput() {
        if (DEFAULT_OBJECT_MAPPER.equals(objectMapper)) {
            objectMapper = ObjectMapperBuilder.defaultObjectMapper().rebirth().get();
        }
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return this;
    }
}
