package com.mayday9.splatoonbot.common.web.request;


import com.mayday9.splatoonbot.common.util.ConvertUtils;
import com.mayday9.splatoonbot.common.util.DateTimeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ParamsMap extends HashMap<String, Object> {
    public String getString(String key) {
        return get(key) == null ? null : get(key).toString();
    }

    public Integer getInteger(String key) {
        return get(key) == null ? null : ConvertUtils.toInteger(get(key).toString(), null);
    }

    public Integer getInteger(String key, Integer defaultvalue) {
        return get(key) == null ? defaultvalue : ConvertUtils.toInteger(get(key).toString(), defaultvalue);
    }

    public Long getLong(String key) {
        return get(key) == null ? null : ConvertUtils.toLong(get(key).toString(), null);
    }

    public Long getLong(String key, Long defaultvalue) {
        return get(key) == null ? defaultvalue : ConvertUtils.toLong(get(key).toString(), defaultvalue);
    }

    public LocalDateTime getLocalDateTime(String key) {
        return get(key) == null ? null : DateTimeUtils.toLocalDateTime(get(key).toString());
    }

    public LocalDate getLocalDate(String key) {
        return get(key) == null ? null : DateTimeUtils.toLocalDate(get(key).toString());
    }

    public Boolean getBoolean(String key) {
        return get(key) == null ? null : ConvertUtils.toBoolean(get(key));
    }
}
