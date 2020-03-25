package com.igniubi.core.aysnc.service;

import com.igniubi.core.aysnc.func.MultipleArgumentsFunction;
import com.igniubi.core.aysnc.model.AysncProviderDefinition;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public interface AysncDataQueryService {
    <T> T get(String name, Map<String,Object> invokeParams, Class<T> resultType)
            throws InterruptedException, InvocationTargetException, IllegalAccessException;

    <T> T get(AysncProviderDefinition provider, Map<String,Object> invokeParams, Class<T> resultType)
            throws InterruptedException, InvocationTargetException, IllegalAccessException;

    <T> T get(AysncProviderDefinition provider, Map<String,Object> invokeParams, Class<T> resultType, boolean useCache)
            throws InterruptedException, InvocationTargetException, IllegalAccessException;

    AysncProviderDefinition getProvider(MultipleArgumentsFunction<?> function) throws IllegalAccessException;

}
