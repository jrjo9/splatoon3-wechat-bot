package com.mayday9.splatoonbot.common.json;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mayday9.splatoonbot.common.db.EnumItemView;
import com.mayday9.splatoonbot.common.enums.IIntegerEnum;
import com.mayday9.splatoonbot.common.util.ConvertUtils;
import com.mayday9.splatoonbot.common.util.DateTimeUtils;
import com.mayday9.splatoonbot.common.util.core.Convert;
import com.mayday9.splatoonbot.common.web.request.SearchDTO;
import com.mayday9.splatoonbot.common.web.request.SearchExtDTO;
import com.mayday9.splatoonbot.common.web.request.SearchRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class JsonSerializerModule extends SimpleModule {
    public JsonSerializerModule() {
        //枚举序列化
        addSerializer(Enum.class, new JsonSerializer<Enum>() {
            @Override
            public void serialize(Enum anEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                if (anEnum instanceof IIntegerEnum) {
                    IIntegerEnum integerEnum = (IIntegerEnum) anEnum;
                    jsonGenerator.writeObject(new EnumItemView(integerEnum.getValue(), integerEnum.getDisplay()));
                } else {
                    jsonGenerator.writeString(anEnum.toString());
                }
            }
        });

        //树举反序列化
        setDeserializerModifier(new BeanDeserializerModifier() {
            //enum反序列化比较特殊 ，需重写BeanDeserializerModifier的enum方法，不然拿不到enum的具体类型
            @SuppressWarnings("unchecked")
            @Override
            public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
                final Class<Enum> rawClass = (Class<Enum>) type.getRawClass();
                return new JsonDeserializer<Enum>() {
                    @Override
                    public Enum deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                        String text = jsonParser.getText();
                        if (StringUtils.isEmpty(text)) {
                            return null;
                        }
                        if ("{".equals(text)) {
                            //enum是对象 ，跳到结束符"}”
                            do {
                                jsonParser.nextValue();
                                if ("value".equals(jsonParser.getCurrentName())) {
                                    text = jsonParser.getText();
                                }
                            } while (!"}".equals(jsonParser.getText()));
                        }
                        Enum[] enums = rawClass.getEnumConstants();
                        for (Enum e : enums) {
                            //iitegerenum按value转为枚举
                            if (IIntegerEnum.class.isAssignableFrom(rawClass) && ConvertUtils.isInt(text)) {
                                if (Objects.equals(((IIntegerEnum) e).getValue(), Integer.parseInt(text))) {
                                    return e;
                                }
                            } else {
                                //默认按名宇转枚举
                                if (e.name().equalsIgnoreCase(text)) {
                                    return e;
                                }
                            }
                        }
                        throw new IllegalArgumentException(text + " could not convert to enum : " + rawClass.getName());
                    }
                };
            }
        });

        //date
        addSerializer(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(date == null ? null : Convert.toString(date, Convert.DATA_FORMAT_DATETIME_SLASH, TimeZone.getTimeZone(ZoneId.systemDefault())));
            }
        });

        addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                String text = jsonParser.getText();
                return DateTimeUtils.toDate(text);
            }
        });

        //LocalDateTime
        addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(localDateTime == null ? null : DateTimeUtils.dateTimeDisplay(localDateTime));
            }
        });
        addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                String text = jsonParser.getText();
                return DateTimeUtils.toLocalDateTime(text);
            }
        });
        //localdate
        addSerializer(LocalDate.class, new JsonSerializer<LocalDate>() {
            @Override
            public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(localDate == null ? null : localDate.toString());
            }
        });
        addDeserializer(LocalDate.class, new JsonDeserializer<LocalDate>() {
            @Override
            public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                return DateTimeUtils.toLocalDate(jsonParser.getText());
            }
        });
        //Loca1Time
        addSerializer(LocalTime.class, new JsonSerializer<LocalTime>() {
            @Override
            public void serialize(LocalTime localTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(localTime == null ? null : localTime.toString());
            }
        });
        addDeserializer(LocalTime.class, new JsonDeserializer<LocalTime>() {
            @Override
            public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                return DateTimeUtils.toLocalTime(jsonParser.getText());
            }
        });

        //SearchRequest反序列化
        addDeserializer(SearchRequest.class, new JsonDeserializer<SearchRequest>() {
            @Override
            public SearchRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                Map map = deserializationContext.readValue(jsonParser, Map.class);
                SearchRequest searchRequest = new SearchRequest();
                if (map != null) {
                    for (Object key : map.keySet()) {
                        if (key != null) {
                            String value = map.get(key) == null ? null : map.get(key).toString();
                            switch (key.toString()) {
                                case "page":
                                    searchRequest.setPage(ConvertUtils.toInteger(value, 1));
                                    break;
                                case "pageSize":
                                    searchRequest.setPageSize(ConvertUtils.toInteger(value, 10));
                                    break;
                                case "beginTime":
                                    searchRequest.setBeginTime(DateTimeUtils.toLocalDateTime(value));
                                    break;
                                case "endTime":
                                    searchRequest.setEndTime(DateTimeUtils.toLocalDateTime(value));
                                    break;
                                case "keyword":
                                    searchRequest.setKeyword(value);
                                    break;
                                case "sortBy":
                                    searchRequest.setSortBy(value);
                                    break;
                                case "sortType":
                                    searchRequest.setSortType(value);
                                    break;
                                default:
                                    searchRequest.setParams(key.toString(), value);
                            }
                        }
                    }
                }
                return searchRequest;
            }
        });

        //SearchDTO反序列化
        addDeserializer(SearchDTO.class, new JsonDeserializer<SearchDTO>() {
            @Override
            public SearchDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                Map map = deserializationContext.readValue(jsonParser, Map.class);
                SearchExtDTO searchDTO = new SearchExtDTO();
                if (null != map) map.put("today", DateUtil.today());
                if (map != null) {
                    for (Object key : map.keySet()) {
                        if (key != null) {
                            String value = map.get(key) == null ? null : map.get(key).toString();
                            switch (key.toString()) {
                                case "pageNum":
                                    searchDTO.setPageNum(Convert.toInt(value, 1));
                                    searchDTO.setParam("pageNum", searchDTO.getPageNum());
                                    break;
                                case "pageSize":
                                    searchDTO.setPageSize(Convert.toInt(value, 10));
                                    searchDTO.setParam("pageSize", searchDTO.getPageSize());
                                    break;
                                case "beginTime":
                                    searchDTO.setBeginTime(DateTimeUtils.toDate(value));
                                    searchDTO.setBeginTimeStr(DateUtil.formatDateTime(searchDTO.getBeginTime()));
                                    searchDTO.setParam("beginTime", searchDTO.getBeginTimeStr());
                                    break;
                                case "endTime":
                                    searchDTO.setEndTime(DateTimeUtils.toDate(value));
                                    searchDTO.setEndTimeStr(DateUtil.formatDateTime(searchDTO.getEndTime()));
                                    searchDTO.setParam("endTime", searchDTO.getEndTimeStr());
                                    break;
                                case "keyword":
                                    searchDTO.setKeyword(value);
                                    searchDTO.setParam("keyword", value);
                                    break;
                                case "sortBy":
                                    searchDTO.setSortBy(value);
                                    searchDTO.setParam("sortBy", value);
                                    break;
                                case "sortType":
                                    searchDTO.setSortType(value);
                                    searchDTO.setParam("sortType", value);
                                    break;
                                default:
                                    searchDTO.setParam(key.toString(), value);
                            }
                        }
                    }
                }
                return searchDTO;
            }
        });
    }
}
