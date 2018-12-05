package com.igniubi.redis.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.igniubi.model.common.RedisKeyEnum;
import io.lettuce.core.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


/**
 * 就一个小Demo 随便写下
 */
@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);


    private final RedisTemplate<String, String> stringRedisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public <T> T get(RedisKeyBuilder keyBuilder, Class<T> tClass) {
        String result;
        T t = null;
        try {
            result = stringRedisTemplate.boundValueOps(keyBuilder.getKey()).get();
            t = JSONObject.parseObject(result, tClass);
        } catch (Exception e) {
            logger.warn("RedisUtil get error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return t;
    }

    public <T> List<T> getList(RedisKeyBuilder keyBuilder, Class<T> clazz) {
        try {
            String result = stringRedisTemplate.boundValueOps(keyBuilder.getKey()).get();
            return JSON.parseArray(result, clazz);
        } catch (Exception e) {
            logger.warn("RedisUtil getList error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return null;
    }

    public void set(RedisKeyBuilder keyBuilder, Object value, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.boundValueOps(keyBuilder.getKey()).set(JSON.toJSONString(value), timeout, unit);
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
    }

    public Long increament(RedisKeyBuilder keyBuilder, long value, long timeout, TimeUnit unit) {
        Long result = null;
        try {
            result = stringRedisTemplate.boundValueOps(keyBuilder.getKey()).increment(value);
            stringRedisTemplate.boundValueOps(keyBuilder.getKey()).expire(timeout, unit);
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }

    public  <T> T  cacheObtain(RedisKeyEnum keyEnum, Object key, Callable<T> callable, Class<T> c){
        RedisKeyBuilder keyBuilder = RedisKeyBuilder.newInstance().appendFixed(keyEnum.getCacheKey()).appendVar(key);

        T t =  this.get(keyBuilder, c);

        if(t != null){
            return t;
        }

        try {
            t = callable.call();
        } catch (Exception e) {
            logger.info("cacheObtain error, e is {}", e);
        }

        if(t != null ){
            this.set(keyBuilder, t, keyEnum.getCacheTime(), keyEnum.getTimeUnit());
        }

        return t;
    }
}