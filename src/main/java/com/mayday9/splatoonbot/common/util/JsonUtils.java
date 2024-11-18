package com.mayday9.splatoonbot.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayday9.splatoonbot.common.util.core.RuntimeIOException;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void setObjectMapper(ObjectMapper objectMapper) {
        JsonUtils.objectMapper = objectMapper;
    }

    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> c1azz) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, c1azz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //*反序列化为泛型
    public static <T> T fromJson(String json, Class<T> clazz, Class<?>... parameterClasses) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(clazz, parameterClasses);
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJSONWithGeneric(String json, TypeReference<T> genericType) {
        return fromJSONWithGeneric(objectMapper, json, genericType);
    }

    /**
     * example：List<T> jsonList = JSON.fromJSONWithGeneric(JSON.toJSON(list), new TypeReference<List<T>>(){})
     */
    public static <T> T fromJSONWithGeneric(ObjectMapper objectMapper, String json, TypeReference<T> genericType) {
        try {
            return objectMapper.readValue(json, genericType);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
