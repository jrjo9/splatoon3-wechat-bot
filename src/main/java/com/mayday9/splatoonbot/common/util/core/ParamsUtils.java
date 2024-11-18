package com.mayday9.splatoonbot.common.util.core;

import java.util.HashMap;
import java.util.Map;

public final class ParamsUtils {

    public static Param addParam(String key, Object obj) {
        return new Param(key, obj);
    }

    public Map<String, Object> param(String key, Object obj) {
        return new Param(key, obj).param();
    }

    public static final class Param {

        private Map<String, Object> params = new HashMap<String, Object>();


        public Param(String key, Object value) {
            addParam(key, value);
        }

        public Param addParam(String key, Object value) {
            this.params.put(key, value);
            return this;
        }

        public Map<String, Object> param() {
            return this.params;
        }
    }
}
