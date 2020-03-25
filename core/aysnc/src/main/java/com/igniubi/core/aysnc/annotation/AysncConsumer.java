package com.igniubi.core.aysnc.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AysncConsumer {

    /**
     * Unique identifier
     */
    @AliasFor("value")
    String name() default "";


    @AliasFor("name")
    String value() default "";

    /**
     * The parameter key required by the method being consumed will be dynamically replaced
     */
    DynamicParameter [] dynamicParameters() default {};

//    /**
//     * Exception handling, default by global configuration
//     */
//    ExceptionProcessingMethod exceptionProcessingMethod()
//            default ExceptionProcessingMethod.BY_DEFAULT;
}
