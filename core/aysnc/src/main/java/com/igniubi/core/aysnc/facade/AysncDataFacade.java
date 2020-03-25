package com.igniubi.core.aysnc.facade;

import com.igniubi.core.aysnc.func.MultipleArgumentsFunction;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class AysncDataFacade {

    @Setter
    private static AysncBeanQueryFacade facade;

    public AysncDataFacade(AysncBeanQueryFacade facade) {
        AysncDataFacade.facade = facade;
    }

    public static <T> T get(String id, Map<String,Object> invokeParams, Class<T> clazz)
            throws InterruptedException, IllegalAccessException, InvocationTargetException {
        return facade.get(id,invokeParams,clazz);
    }

    public static  <T> T get(Map<String,Object> invokeParams, MultipleArgumentsFunction<T> multipleArgumentsFunction, Long timeout)
            throws InterruptedException, IllegalAccessException, InvocationTargetException {
        return facade.get(invokeParams,multipleArgumentsFunction,timeout);
    }

    //不是用缓存(包括provider也不使用缓存)   使用场景 lambda
    public static  <T> T get(MultipleArgumentsFunction<T> multipleArgumentsFunction, Long timeout)
            throws InterruptedException, IllegalAccessException, InvocationTargetException {
        return facade.get(multipleArgumentsFunction, timeout);
    }
}
