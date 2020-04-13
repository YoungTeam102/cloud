package com.igniubi.core.aysnc.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InvokeParameter {
    /**
     * Manually passed parameter key
     */
    @AliasFor("key")
    String value() default "";

    /**
     * same as value();
     */
    @AliasFor("value")
    String key() default "";
}
