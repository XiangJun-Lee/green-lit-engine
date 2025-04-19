package com.keji.green.lit.engine.utils;

import com.alibaba.fastjson.JSON;

/**
 * JSON工具类
 *
 * @author xiangjun_lee
 * @since 2024-04-11
 */
public class JsonUtils {

    /**
     * 将对象转换为JSON字符串
     *
     * @param object 要转换的对象
     * @return JSON字符串
     */
    public static String toJson(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * 将JSON字符串转换为指定类型的对象
     *
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
} 