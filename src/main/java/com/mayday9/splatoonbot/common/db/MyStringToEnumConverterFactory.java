package com.mayday9.splatoonbot.common.db;

import com.mayday9.splatoonbot.common.enums.IIntegerEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;

public class MyStringToEnumConverterFactory implements ConverterFactory<String, IIntegerEnum> {

    private static final Map<Class, Converter> CONVERTER_MAP = new HashMap<>();

    @Override
    public <T extends IIntegerEnum> Converter<String, T> getConverter(Class<T> targetType) {
        Converter<String, T> converter = CONVERTER_MAP.get(targetType);
        if (converter == null) {
            converter = new StringToEnumConverter<>(targetType);
            CONVERTER_MAP.put(targetType, converter);
        }
        return converter;
    }

    class StringToEnumConverter<T extends IIntegerEnum> implements Converter<String, T> {
        private Map<String, T> enumMap = new HashMap<>();

        StringToEnumConverter(Class<T> enumType) {
            T[] enums = enumType.getEnumConstants();
            for (T e : enums) {
                enumMap.put(String.valueOf(e.getValue()), e);
            }
        }

        @Override
        public T convert(String source) {

            T t = enumMap.get(source);
            if (t == null) {
                // 异常可以稍后去捕获
                throw new IllegalArgumentException("No element matches " + source);
            }
            return t;
        }
    }
}
