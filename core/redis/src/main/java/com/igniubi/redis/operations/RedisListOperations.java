package com.igniubi.redis.operations;

import com.igniubi.redis.util.RedisKeyBuilder;
import com.igniubi.redis.util.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RedisListOperations {

    private static final Logger logger = LoggerFactory.getLogger(RedisListOperations.class);

    private final RedisTemplate<String, String> redisTemplate;

    private ListOperations<String, String> listOperations;

    @Autowired
    public RedisListOperations(RedisTemplate<String, String> stringRedisTemplate) {
        this.redisTemplate = stringRedisTemplate;
        listOperations = redisTemplate.opsForList();
    }


    public <T> void rightPush(RedisKeyBuilder keyBuilder, T o, long time, TimeUnit unit) {
        try {
            String value = SerializeUtils.value2String(o);
            listOperations.rightPush(keyBuilder.getKey(), value);
            redisTemplate.expire(keyBuilder.getKey(), time, unit);
        } catch (Exception e) {
            logger.warn("RedisUtil get error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
    }

    public <T> void leftPush(RedisKeyBuilder keyBuilder, T o, long time, TimeUnit unit) {
        try {
            String value = SerializeUtils.value2String(o);
            listOperations.leftPush(keyBuilder.getKey(), value);
            redisTemplate.expire(keyBuilder.getKey(), time, unit);
        } catch (Exception e) {
            logger.warn("RedisUtil get error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
    }

    public Long size(RedisKeyBuilder keyBuilder) {
        try {
            return listOperations.size(keyBuilder.getKey());
        } catch (Exception e) {
            logger.warn("RedisUtil getList error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return 0L;
    }

    public <T> List<T> range(RedisKeyBuilder keyBuilder, long start, long end, Class<T> aClass) {
        List<String> result = null;
        List<T> v = null;
        try {
            result = listOperations.range(keyBuilder.getKey(), start, end);
            if (result == null) {
                return null;
            }
            v = result.stream().map(s -> SerializeUtils.string2Value(s, aClass)).collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return v;
    }



    public void trim (RedisKeyBuilder keyBuilder, long start, long end) {
        try {
            listOperations.trim(keyBuilder.getKey(), start, end);
        } catch (Exception e) {
            logger.warn("RedisUtil trim error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
    }

}
