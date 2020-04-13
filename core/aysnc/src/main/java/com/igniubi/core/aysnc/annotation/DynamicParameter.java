package com.igniubi.core.aysnc.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicParameter {
    /**
     *  Method originally required parameter key.
     */
    String targetKey();

    /**
     * The new key used to replace the original parameter key
     */
    String replacementKey();
}
