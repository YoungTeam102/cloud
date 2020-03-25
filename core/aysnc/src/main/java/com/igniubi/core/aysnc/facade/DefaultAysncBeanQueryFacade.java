package com.igniubi.core.aysnc.facade;

import com.igniubi.core.aysnc.func.MultipleArgumentsFunction;
import com.igniubi.core.aysnc.model.AysncProviderDefinition;
import com.igniubi.core.aysnc.service.AysncDataQueryService;
import com.igniubi.core.aysnc.utils.DefinitionUtils;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;

public class DefaultAysncBeanQueryFacade implements  AysncBeanQueryFacade{

    @Setter
    private AysncDataQueryService aysncDataQueryService;

    @Override
    public <T> T get(String id, Map<String, Object> invokeParams, Class<T> clazz) throws InterruptedException, IllegalAccessException, InvocationTargetException {
        return aysncDataQueryService.get(id, invokeParams, clazz);
    }

    @Override
    public <T> T get(Map<String, Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction, Long timeout)  throws InterruptedException, IllegalAccessException, InvocationTargetException{
        if(invokeParams == null) {
            invokeParams = Collections.emptyMap();
        }

        AysncProviderDefinition provider = aysncDataQueryService.getProvider(multipleArgumentsFunction);
        Method applyMethod = provider.getMethod();
        boolean accessible = applyMethod.isAccessible();
        if(! accessible) {
            applyMethod.setAccessible(true);
        }
        try {
            @SuppressWarnings("unchecked")
            T ret = (T) aysncDataQueryService.get(provider, invokeParams, applyMethod.getReturnType());
            return ret;
        } finally {
            if(!accessible) {
                applyMethod.setAccessible(accessible);
            }
        }
    }

    @Override
    public <T> T get(MultipleArgumentsFunction<T> multipleArgumentsFunction, Long timeout) throws InterruptedException, IllegalAccessException, InvocationTargetException {
        Method[] methods = multipleArgumentsFunction.getClass().getMethods();
        Method applyMethod = null;

        for (Method method : methods) {
            if(! Modifier.isStatic(method.getModifiers()) && ! method.isDefault()) {
                applyMethod = method;
                break;
            }
        }

        if(applyMethod == null) {
            throw new IllegalAccessException(multipleArgumentsFunction.getClass().getName());
        }
        AysncProviderDefinition provider = DefinitionUtils.getProvideDefinition(applyMethod);
        provider.setTarget(multipleArgumentsFunction);
        provider.setName(multipleArgumentsFunction.getClass().getName());
        provider.setUseCache(false);
        applyMethod = provider.getMethod();
        boolean accessible = applyMethod.isAccessible();
        if(! accessible) {
            applyMethod.setAccessible(true);
        }
        try {
            @SuppressWarnings("unchecked")
            T ret = (T) aysncDataQueryService.get(provider,  Collections.emptyMap(), applyMethod.getReturnType());
            return ret;
        } finally {
            if(!accessible) {
                applyMethod.setAccessible(accessible);
            }
        }
    }
}
