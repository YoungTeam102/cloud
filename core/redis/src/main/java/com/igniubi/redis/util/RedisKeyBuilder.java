package com.igniubi.redis.util;


/**
 * 描述:Redis 缓存的统一Key的构建类。
 */
public class RedisKeyBuilder {

    /**
     * redis的key
     */
    private StringBuilder key=new StringBuilder();

    /**
     * redis key的分组模板
     */
    private StringBuilder keyTemplate=new StringBuilder();

    /**
     * 防止getKey时重复的new String；
     */
    private String innerkey;

    /**
     * 防止getKeyTemplate时重复的new String；
     */
    private String innerKeyTemplate;


    public static final String SEPARATOR_MH=":";

    public static final String SEPARATOR_JH="#";

    private static final String PLACEHOLDER="{}";

    public RedisKeyBuilder() {
    }

    public static RedisKeyBuilder newInstance(){
        return new RedisKeyBuilder();
    }

    public RedisKeyBuilder appendFixed(Object fixedArg) {
        key.append(fixedArg);
        keyTemplate.append(fixedArg);
        return  this;
    }

    public RedisKeyBuilder appendVar(Object varArg) {
        key.append(varArg);
        keyTemplate.append(PLACEHOLDER);
        return  this;
    }

    public RedisKeyBuilder appendVarWithMH(Object... vars){
        appendVarWithSeparator(SEPARATOR_MH,vars);
        return this;
    }

    public RedisKeyBuilder appendVarWithSeparator(String separator,Object... vars){
        if(vars.length==0){
            return this;
        }
        for(Object var:vars){
            appendVar(var).appendFixed(separator);
        }
        return this;
    }


    public String getKey() {
        if(innerkey!=null){
            return innerkey;
        }
        innerkey=key.toString();
        return innerkey;
    }

    public String getKeyTemplate() {
        if(innerKeyTemplate!=null){
            return innerKeyTemplate;
        }
        innerKeyTemplate=keyTemplate.toString();
        return innerKeyTemplate;
    }

    @Override
    public String toString() {
        return getKey();
    }
}
