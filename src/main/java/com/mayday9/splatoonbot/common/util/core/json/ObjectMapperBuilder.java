package com.mayday9.splatoonbot.common.util.core.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class ObjectMapperBuilder {
    static final ObjectMapper DEFAULT_OBJECT_MAPPER;

    static {
        DEFAULT_OBJECT_MAPPER = createMapper();
    }

    public static ObjectMapper getObjectMapper() {
        return DEFAULT_OBJECT_MAPPER;
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new JSONDateFormat());
        AnnotationIntrospector primary = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();
        mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(primary, secondary));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        return mapper;
    }

    private boolean changed = false;
    private ObjectMapper objectMapper;

    public ObjectMapperBuilder() {
        this.objectMapper = DEFAULT_OBJECT_MAPPER;
    }

    public ObjectMapper get() {
        return objectMapper;
    }

    public static ObjectMapperBuilder defaultObjectMapper() {
        return new ObjectMapperBuilder();
    }

    public ObjectMapperBuilder rebirth() {
        if (!changed && DEFAULT_OBJECT_MAPPER.equals(objectMapper)) {
            objectMapper = createMapper();
        }
        return this;
    }

    public ObjectMapperBuilder indent() {
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        changed = true;
        return this;
    }
}
