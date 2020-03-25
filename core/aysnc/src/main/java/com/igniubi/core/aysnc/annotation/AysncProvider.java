package com.igniubi.core.aysnc.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AysncProvider {

    /**
     * Unique identifier  全局唯一
     */
    @AliasFor("value")
    String name() default "";


    @AliasFor("name")
    String value() default "";

    //超时时间
    long timeout() default -1;

    //是否幂等
    boolean idempotent() default true;

    boolean useCache() default true;
}
