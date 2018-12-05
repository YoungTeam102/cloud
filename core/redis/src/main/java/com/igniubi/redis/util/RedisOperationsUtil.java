package com.igniubi.redis.util;

import com.igniubi.model.common.RedisKeyEnum;
import com.igniubi.redis.operations.RedisValueOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class RedisOperationsUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisOperationsUtil.class);

    public   static  <T> T cacheObtain(RedisValueOperations operations , RedisKeyEnum keyEnum, Object key, Callable<T> callable, Class<T> c){
        RedisKeyBuilder keyBuilder = RedisKeyBuilder.newInstance().appendFixed(keyEnum.getCacheKey()).appendVar(key);

        T t =  operations.get(keyBuilder, c);

        logger.info(" cacheObtain from cache success, key is {} , result is {}" , keyBuilder.getKey() , t);
        if(t != null){
            return t;
        }

        try {
            t = callable.call();
            logger.info(" cacheObtain from callable success, key is {} , result is {}" , keyBuilder.getKey() , t);
        } catch (Exception e) {
            logger.info("cacheObtain error, e is {}", e);
        }

        if(t != null ){
            operations.set(keyBuilder, t, keyEnum.getCacheTime(), keyEnum.getTimeUnit());
        }

        return t;
    }
}
