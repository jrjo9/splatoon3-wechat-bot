package com.mayday9.splatoonbot.common.util.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MapObjUtil {
    /**
     * 实体对象转成Map
     *
     * @param obj 实体对象
     * @return
     */
    public static Map<String, Object> object2Map(Object obj) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (obj == null) {
            return map;
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return map;
    }

    /**
     * Map转成实体对象
     *
     * @param map   map实体对象包含属性
     * @param clazz 实体对象类型
     * @return
     */
    public static <T> T map2Object(Map<String, Object> map, Class<T> clazz) {
        if (map == null) {
            return null;
        }
        T obj = null;
        try {
            obj = clazz.newInstance();

            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                String filedTypeName = field.getType().getName();
                String fieldName = field.getName().toLowerCase();
                if (filedTypeName.equalsIgnoreCase("java.util.date")) {
                    if (map.containsKey(field.getName().toLowerCase())) {
                        String datetimeStamp = String.valueOf(map.get(fieldName));

                        if (datetimeStamp.equalsIgnoreCase("null")) {
                            field.set(obj, null);
                        } else {
                            field.set(obj, new Date(Long.parseLong(datetimeStamp)));
                        }
                    }
                } else {
                    if (map.containsKey(field.getName().toLowerCase()) && (!String.valueOf(map.get(fieldName)).equals("null"))) {
                        field.set(obj, map.get(fieldName));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return obj;
    }

    /**
     * Map转成实体对象，map带下划线
     *
     * @param map       map实体对象包含属性
     * @param beanClass 实体对象类型
     * @param <T>
     * @return
     */
    public static <T> T map2ObjectUnderlined(Map<String, Object> map, Class<T> beanClass) {
        if (map == null) {
            return null;
        }
        Map<String, Object> data = toReplaceKeyLow(map);
        return map2Object(data, beanClass);
    }


    private static String underlineToCamel2(String param) {
        param = param.toLowerCase();//此处为全部转小写，方便根据_判定后一位转驼峰
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        //使用正则表达式
        StringBuilder sb = new StringBuilder(param);
        Matcher mc = Pattern.compile("_").matcher(param);
        int i = 0;
        while (mc.find()) {
            int position = mc.end() - (i++);
            //String.valueOf(Character.toUpperCase(sb.charAt(position)));
            sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
        }
        return sb.toString();
    }

    /**
     * map转化，将map key下划线去掉
     *
     * @param map 原map
     * @return
     */
    private static Map<String, Object> toReplaceKeyLow(Map<String, Object> map) {
        Map re_map = new HashMap();
        if (re_map != null) {
//            Iterator var2 = map.entrySet().iterator();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                re_map.put(underlineToCamel2(entry.getKey().trim()), entry.getValue());
            }
//            while(var2.hasNext()) {
//                Entry<String, Object> entry = (Entry)var2.next();
//                re_map.put(underlineToCamel2((String)entry.getKey()), map.get(entry.getKey()));
//            }

            map.clear();
        }

        return re_map;
    }


    /**
     * Map中根据key批量删除键值对
     *
     * @param map
     * @param excludeKeys
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map removeEntries(Map<K, V> map, K[] excludeKeys) {
        Iterator<K> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            K k = iterator.next();
            // 如果k刚好在要排除的key的范围中
            if (ArrayUtils.contains(excludeKeys, k)) {
                iterator.remove();
                map.remove(k);
            }
        }
        return map;
    }

}

