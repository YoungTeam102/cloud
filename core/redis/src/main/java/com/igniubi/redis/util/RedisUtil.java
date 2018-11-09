package com.igniubi.redis.util;

import com.alibaba.fastjson.JSON;
import io.lettuce.core.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 就一个小Demo 随便写下
 *
 */
@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);


    private final RedisTemplate<String, String> stringRedisTemplate;

    @Autowired
    public RedisUtil(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public String get(RedisKeyBuilder keyBuilder)   {
        String result = null;
        try {
            result = stringRedisTemplate.boundValueOps(keyBuilder.getKey()).get();
        } catch (Exception e){
            logger.warn("RedisUtil get error, key is {}, e is {}",keyBuilder.getKey(), e);
        }
        return result;
    }

    public <T>  List<T> getList(RedisKeyBuilder keyBuilder, Class<T> clazz)   {
        try {
            String result = stringRedisTemplate.boundValueOps(keyBuilder.getKey()).get();
            return JSON.parseArray(result, clazz);
        } catch (Exception e){
            logger.warn("RedisUtil getList error, key is {}, e is {}",keyBuilder.getKey(), e);
        }
        return null;
    }

    public void set(RedisKeyBuilder keyBuilder, Object value, long timeout, TimeUnit unit)  {
        try {
            stringRedisTemplate.boundValueOps(keyBuilder.getKey()).set(JSON.toJSONString(value),timeout,unit);
        } catch (Exception e){
            logger.warn("RedisUtil set error, key is {}, e is {}",keyBuilder.getKey(), e);
        }
    }

    public Long increament(RedisKeyBuilder keyBuilder, long value, long timeout, TimeUnit unit)  {
        Long result = null;
        try {
            result = stringRedisTemplate.boundValueOps(keyBuilder.getKey()).increment(value);
            stringRedisTemplate.boundValueOps(keyBuilder.getKey()).expire(timeout,unit);
        } catch (Exception e){
            logger.warn("RedisUtil set error, key is {}, e is {}",keyBuilder.getKey(), e);
        }
        return result;
    }

}