package com.mayday9.splatoonbot.common.util.core.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.mayday9.splatoonbot.common.util.core.RuntimeIOException;

import java.io.IOException;

public class JSON {

    public static String toJSON(Object obj) {
        return toJSON(ObjectMapperBuilder.defaultObjectMapper(), obj);
    }

    public static String toJSON(ObjectMapperBuilder builder, Object obj) {
        try {
            return builder.get().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeIOException(e);
        }
    }

    public static <T> T fromJSON(String json, Class<T> exceptClass) {
        return fromJSON(ObjectMapperBuilder.defaultObjectMapper(), json, exceptClass);
    }

    public static <T> T fromJSON(ObjectMapperBuilder builder, String json, Class<T> exceptClass) {
        try {
            return builder.get().readValue(json, exceptClass);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    public static <T> T fromJSONWithGeneric(String json, TypeReference<T> genericType) {
        return fromJSONWithGeneric(ObjectMapperBuilder.defaultObjectMapper(), json, genericType);
    }

    /**
     * example：List<T> jsonList = JSON.fromJSONWithGeneric(JSON.toJSON(list), new TypeReference<List<T>>(){})
     */
    public static <T> T fromJSONWithGeneric(ObjectMapperBuilder builder, String json, TypeReference<T> genericType) {
        try {
            return builder.get().readValue(json, genericType);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    public static <T> T fromJSONWithGeneric(String json, JavaType javaType) {
        return fromJSONWithGeneric(ObjectMapperBuilder.defaultObjectMapper(), json, javaType);
    }

    /**
     * @param builder  {@see ObjectMapperBuilder}
     * @param json     value
     * @param javaType {@link com.fasterxml.jackson.databind.type.TypeFactory#constructParametricType} or {@link com.fasterxml.jackson.databind.ObjectMapper#constructType}
     * @param <T>      result Type
     * @return result
     */
    public static <T> T fromJSONWithGeneric(ObjectMapperBuilder builder, String json, JavaType javaType) {
        try {
            return builder.get().readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
