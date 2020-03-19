package com.igniubi.redis.operations;

import com.igniubi.redis.util.RedisKeyBuilder;
import com.igniubi.redis.util.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RedisZsetOperations {

    private static final Logger logger = LoggerFactory.getLogger(RedisZsetOperations.class);

    private final RedisTemplate<String, String> redisTemplate;
    private ZSetOperations<String, String> zSetOperations;

    @Autowired
    public RedisZsetOperations(RedisTemplate<String, String> stringRedisTemplate) {
        this.redisTemplate = stringRedisTemplate;
        zSetOperations = redisTemplate.opsForZSet();
    }


    public <T> Boolean zAdd(RedisKeyBuilder keyBuilder, T o, Double score, long time, TimeUnit unit) {
        Boolean result = Boolean.FALSE;
        try {
            String value = SerializeUtils.value2String(o);
            result = zSetOperations.add(keyBuilder.getKey(), value, score);
            redisTemplate.expire(keyBuilder.getKey(), time, unit);
        } catch (Exception e) {
            logger.warn("RedisUtil get error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }

    public <T> Long remove(RedisKeyBuilder keyBuilder, T o) {
        try {
            String value = SerializeUtils.value2String(o);
            return zSetOperations.remove(keyBuilder.getKey(), value);
        } catch (Exception e) {
            logger.warn("RedisUtil getList error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return null;
    }

    public <T> Long multiRemove(RedisKeyBuilder keyBuilder, List<T> oList) {
        try {
            List<String> vlist = oList.stream().map(SerializeUtils::value2String).collect(Collectors.toList());
            return zSetOperations.remove(keyBuilder.getKey(), vlist);
        } catch (Exception e) {
            logger.warn("RedisUtil getList error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return null;
    }

    public <T> Double incrementScore(RedisKeyBuilder keyBuilder, T o, long score) {
        Double result = null;
        try {
            String value = SerializeUtils.value2String(o);
            result = zSetOperations.incrementScore(keyBuilder.getKey(), value, score);
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }


    public <T> Set<T> range(RedisKeyBuilder keyBuilder, long start, long end, Class<T> aClass) {
        Set<String> result = null;
        Set<T> v = null;
        try {
            result = zSetOperations.range(keyBuilder.getKey(), start, end);
            if (result == null) {
                return null;
            }
            v = result.stream().map(s -> SerializeUtils.string2Value(s, aClass)).collect(Collectors.toSet());
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return v;
    }

    public <T> Set<T> rangeByScore(RedisKeyBuilder keyBuilder, double min, double max, Class<T> aClass) {
        Set<String> result = null;
        Set<T> v = null;
        try {
            result = zSetOperations.rangeByScore(keyBuilder.getKey(), min, max);
            if (result == null) {
                return null;
            }
            v = result.stream().map(s -> SerializeUtils.string2Value(s, aClass)).collect(Collectors.toSet());
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return v;
    }

    public Set<ZSetOperations.TypedTuple<String>> range(RedisKeyBuilder keyBuilder, long start, long end) {
        Set<ZSetOperations.TypedTuple<String>> result = null;
        try {
            result = zSetOperations.rangeWithScores(keyBuilder.getKey(), start, end);
            if (result == null) {
                return null;
            }
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }


    public <T> Set<T> reverseRange(RedisKeyBuilder keyBuilder, long start, long end, Class<T> aClass) {
        Set<String> result = null;
        Set<T> v = null;
        try {
            result = zSetOperations.reverseRange(keyBuilder.getKey(), start, end);
            if (result == null) {
                return null;
            }
            v = result.stream().map(s -> SerializeUtils.string2Value(s, aClass)).collect(Collectors.toSet());
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return v;
    }

    public <T> Set<T> reverseRangeByScore(RedisKeyBuilder keyBuilder, double min, double max, Class<T> aClass) {
        Set<String> result = null;
        Set<T> v = null;
        try {
            result = zSetOperations.reverseRangeByScore(keyBuilder.getKey(), min, max);
            if (result == null) {
                return null;
            }
            v = result.stream().map(s -> SerializeUtils.string2Value(s, aClass)).collect(Collectors.toSet());
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return v;
    }

    public Set<ZSetOperations.TypedTuple<String>> reverseRangeWithScores(RedisKeyBuilder keyBuilder, long start, long end) {
        Set<ZSetOperations.TypedTuple<String>> result = null;
        try {
            result = zSetOperations.reverseRangeWithScores(keyBuilder.getKey(), start, end);
            if (result == null) {
                return null;
            }
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }

    public <T> Long rank(RedisKeyBuilder keyBuilder, T o) {
        Long result = null;
        try {
            String value = SerializeUtils.value2String(o);
            result = zSetOperations.rank(keyBuilder.getKey(), value);
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }

    public <T> Long reverseRank(RedisKeyBuilder keyBuilder, T o) {
        Long result = null;
        try {
            String value = SerializeUtils.value2String(o);
            result = zSetOperations.reverseRank(keyBuilder.getKey(), value);
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }

    public Long count(RedisKeyBuilder keyBuilder, double min, double max) {
        Long result = null;
        try {
            result = zSetOperations.count(keyBuilder.getKey(), min, max);
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }

    public Long size(RedisKeyBuilder keyBuilder) {
        Long result = null;
        try {
            result = zSetOperations.size(keyBuilder.getKey());
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }

    public <T> Double score(RedisKeyBuilder keyBuilder, T o) {
        Double result = null;
        try {
            String value = SerializeUtils.value2String(o);
            result = zSetOperations.score(keyBuilder.getKey(), value);
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }

    public Long unionAndStore(RedisKeyBuilder keyBuilder, Collection<String> otherKeys, String destKey) {
        Long result = null;
        try {
            result = zSetOperations.unionAndStore(keyBuilder.getKey(), otherKeys, destKey);
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }

    public Long unionAndStore(RedisKeyBuilder keyBuilder, String otherKey, String destKey) {
        Long result = null;
        try {
            result = zSetOperations.unionAndStore(keyBuilder.getKey(), otherKey, destKey);
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }

    public Long unionAndStoreByMax(RedisKeyBuilder keyBuilder, Collection<String> otherKeys, String destKey) {
        Long result = null;
        try {
            result = zSetOperations.unionAndStore(keyBuilder.getKey(), otherKeys, destKey, RedisZSetCommands.Aggregate.MAX,
                    RedisZSetCommands.Weights.of(1)
            );
        } catch (Exception e) {
            logger.warn("RedisUtil set error, key is {}, e is {}", keyBuilder.getKey(), e);
        }
        return result;
    }
}
