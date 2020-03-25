package com.igniubi.core.aysnc.facade;

import com.igniubi.core.aysnc.func.MultipleArgumentsFunction;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface AysncBeanQueryFacade {

    <T> T get(String id, Map<String,Object> invokeParams, Class<T> clazz)
            throws InterruptedException, IllegalAccessException, InvocationTargetException;

    <T> T get(Map<String,Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction, Long timeout)
            throws InterruptedException, IllegalAccessException, InvocationTargetException;

    <T> T get( MultipleArgumentsFunction<T> multipleArgumentsFunction, Long timeout)
            throws InterruptedException, IllegalAccessException, InvocationTargetException;
}
