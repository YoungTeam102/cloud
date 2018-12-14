package com.igniubi.redis.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

public class SerializeUtils {

    /**
     * 序列化value值
     *
     * @param value
     * @return
     */
    public static <T> String value2String(T value) {
        String v;
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            v = (String) value;
        } else {
            v = JSON.toJSONString(value);
        }
        return v;
    }

    /**
     * 反序列化value值
     *
     * @param value
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T string2Value(String value, Class<T> clazz) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (clazz.getName().equalsIgnoreCase("java.lang.String")) {
            return (T) value;
        }
        return JSON.parseObject(value, clazz);
    }

}
