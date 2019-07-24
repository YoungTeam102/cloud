package com.igniubi.redis.operations;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.igniubi.redis.util.RedisKeyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisValueOperations{

    private static final Logger logger = LoggerFactory.getLogger(RedisValueOperations.class);

    private  RedisTemplate<String, String> stringRedisTemplate;

    private ValueOperations<String, String> valueOperations;

    @Autowired
    public RedisValueOperations(RedisTemplate<String, String> stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        valueOperations = stringRedisTemplate.opsForValue();
    }
    public <T> T get(RedisKeyBuilder keyBuilder, Class<T> tClass) {
        String result;
        T t = null;
        try {
            result =valueOperations.get(keyBuilder.getKey());
            t = JSONObject.parseObject(result, tClass);
        } catch (Exception e) {
            logger.warn("RedisUtil get error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return t;
    }

    public <T> List<T> getList(RedisKeyBuilder keyBuilder, Class<T> clazz) {
        try {
            String result =valueOperations.get(keyBuilder.getKey());
            return JSON.parseArray(result, clazz);
        } catch (Exception e) {
            logger.warn("RedisUtil getList error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return null;
    }

    public void set(RedisKeyBuilder keyBuilder, Object value, long timeout, TimeUnit unit) {
        try {
            valueOperations.set(keyBuilder.getKey(), JSON.toJSONString(value), timeout, unit );
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
    }

    public Long increament(RedisKeyBuilder keyBuilder, long value, long timeout, TimeUnit unit) {
        Long result = null;
        try {
            result = valueOperations.increment(keyBuilder.getKey(), value);
            stringRedisTemplate.boundValueOps(keyBuilder.getKey()).expire(timeout, unit);
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }

}
