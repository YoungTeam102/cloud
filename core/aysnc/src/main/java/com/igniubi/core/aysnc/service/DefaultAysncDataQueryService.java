package com.igniubi.core.aysnc.service;

import com.igniubi.core.aysnc.config.RuntimeSettings;
import com.igniubi.core.aysnc.func.MultipleArgumentsFunction;
import com.igniubi.core.aysnc.model.*;
import com.igniubi.core.aysnc.repository.AysncProviderRepository;
import com.igniubi.core.aysnc.utils.DefinitionUtils;
import lombok.Setter;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class DefaultAysncDataQueryService implements  AysncDataQueryService {

    @Setter
    private AysncProviderRepository repository;

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private ExecutorService executorService;

    @Setter
    private RuntimeSettings runtimeSettings;


    @Override
    public <T> T get(String name, Map<String, Object> invokeParams, Class<T> resultType) throws InterruptedException, InvocationTargetException, IllegalAccessException {
        return get(repository.get(name), invokeParams, resultType);
    }

    @Override
    public <T> T get(AysncProviderDefinition provider, Map<String, Object> invokeParams, Class<T> resultType)
            throws InterruptedException, InvocationTargetException, IllegalAccessException {
        AysncQueryContext queryContext = initQueryContext(provider);
        return innerGet(provider,invokeParams,resultType,null,queryContext);
    }

    @Override
    public <T> T get(AysncProviderDefinition provider, Map<String, Object> invokeParams, Class<T> resultType, boolean useCache) throws InterruptedException, InvocationTargetException, IllegalAccessException {
        AysncQueryContext queryContext = initQueryContext(provider);
        queryContext.setUseCache(useCache);
        return innerGet(provider,invokeParams,resultType,null,queryContext);
    }


    private  <T> T innerGet(AysncProviderDefinition provider, Map<String, Object> invokeParams, Class<T> resultType,
                            AysncConsumerDefinition causedConsumer,AysncQueryContext queryContext)
            throws InterruptedException, InvocationTargetException, IllegalAccessException{
        Map<String,Object> dependObjectMap;
        if(provider.getDepends() != null && ! provider.getDepends().isEmpty()) {
            List<AysncConsumerDefinition> consumeDefinitions = provider.getDepends();
            Long timeout = provider.getTimeout() != null ? provider.getTimeout() : runtimeSettings.getTimeout();
            dependObjectMap = getDependObjectMap(invokeParams, consumeDefinitions, timeout, queryContext);
        } else {
            dependObjectMap = Collections.emptyMap();
        }
        /* 拼凑dependObjects和invokeParams */
        Object [] args = new Object[provider.getMethod().getParameterCount()];

        for (int i = 0 ; i < provider.getMethodArgs().size(); i ++) {
            MethodArg methodArg = provider.getMethodArgs().get(i);
            if (methodArg.getDependType().equals(DependType.OTHER_MODEL)) {
                args[i] = dependObjectMap.get(methodArg.getAnnotationKey() + "_" + methodArg.getParameter().getName());
            } else {
                String paramKey ;
                if(causedConsumer != null && causedConsumer.getDynamicParameterKeyMap() != null
                        && causedConsumer.getDynamicParameterKeyMap().containsKey(methodArg.getAnnotationKey())) {
                    paramKey = causedConsumer.getDynamicParameterKeyMap().get(methodArg.getAnnotationKey());
                } else {
                    paramKey = methodArg.getAnnotationKey();
                }
                args[i] = invokeParams.get(paramKey);
            }
            if (args[i] != null && ! methodArg.getParameter().getType().isAssignableFrom(args[i].getClass())) {
                throw new IllegalArgumentException("param type not match, param:"
                        + methodArg.getParameter().getName());
            }
        }
        // 在一次查询中，对于幂等且参数一致的查询方法结果作缓存
        InvokeSignature invokeSignature = new InvokeSignature(provider.getMethod(),args);
        Object resultModel;
        if(provider.isIdempotent() && queryContext.existCache(invokeSignature)) {
            resultModel = queryContext.getCache(invokeSignature);
        }
        else {
            resultModel = provider.getMethod()
                    .invoke(provider.getTarget() == null
                            ? applicationContext.getBean(provider.getMethod().getDeclaringClass())
                            : provider.getTarget(), args);
            if(provider.isIdempotent()) {
                /* Map 中可能不能放空value */
                queryContext.putCache(invokeSignature,resultModel != null ? resultModel : new Object());
            }
        }
        Object result = resultModel != new Object() ? resultModel : null;
        return resultType.cast(result);
    }

    private Map<String, Object> getDependObjectMap(Map<String, Object> invokeParams,
                                                   List<AysncConsumerDefinition> consumeDefinitions,
                                                   Long timeout,AysncQueryContext queryContext)
            throws InterruptedException, InvocationTargetException, IllegalAccessException {
        Map<String, Object> dependObjectMap;
        CountDownLatch stopDownLatch = new CountDownLatch(consumeDefinitions.size());
        Map<String, Future<?>> futureMap = new HashMap<>(consumeDefinitions.size());
        dependObjectMap = new HashMap<>(consumeDefinitions.size());
        Map<String,AysncConsumerDefinition> consumeDefinitionMap = new HashMap<>(consumeDefinitions.size());
        for (AysncConsumerDefinition depend : consumeDefinitions) {
            consumeDefinitionMap.put(depend.getName(),depend);

            Future<?> future = executorService.submit(new AbstractAsyncQueryTask<Object>(Thread.currentThread()) {
                @Override
                public Object execute() throws Exception {
                    try {
                        Object o = innerGet(repository.get(depend.getName()),invokeParams, depend.getClazz(),depend,queryContext);
                        return depend.getClazz().cast(o);
                    } finally {
                        stopDownLatch.countDown();
                    }
                }
            });
            futureMap.put(depend.getName() + "_" + depend.getOriginalParameterName(),future);
        }
        stopDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        if(! futureMap.isEmpty()){
            for (Map.Entry<String,Future<?>> item : futureMap.entrySet()) {
                Future<?> future = item.getValue();
                Object value = null;
                AysncConsumerDefinition consumeDefinition = consumeDefinitionMap.get(item.getKey().substring(0,item.getKey().indexOf('_')));
                try {
                    value = future.get();
                } catch (ExecutionException e) {
                    if (consumeDefinition.getIgnoreException() != null ? ! consumeDefinition.getIgnoreException()
                            : ! runtimeSettings.isIgnoreException()) {
                        throwException(e);
                    }
                }
                dependObjectMap.put(item.getKey(),value);
            }
        }
        return dependObjectMap;
    }

    private void throwException(ExecutionException e)  throws InterruptedException,
            InvocationTargetException, IllegalAccessException  {
        Throwable cause = e.getCause();
        if (cause instanceof InterruptedException) {
            throw (InterruptedException) cause;
        } else if (cause instanceof  InvocationTargetException){
            throw (InvocationTargetException) cause;
        } else if (cause instanceof IllegalAccessException) {
            throw (IllegalAccessException) cause;
        } else {
            throw (RuntimeException) cause;
        }
    }

    private AysncQueryContext initQueryContext(AysncProviderDefinition rootProvider) {
        AysncQueryContext queryContext = new AysncQueryContext();
        queryContext.setRootThread(Thread.currentThread());
        queryContext.setRootProviderDefinition(rootProvider);
        queryContext.setUseCache(rootProvider.isUseCache());
        return queryContext;
    }

    @Override
    public AysncProviderDefinition getProvider(MultipleArgumentsFunction<?> multipleArgumentsFunction) throws IllegalAccessException {
        AysncProviderDefinition provider = repository.get(multipleArgumentsFunction.getClass().getName());
        if(provider != null) {
            return provider;
        }
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

        provider = DefinitionUtils.getProvideDefinition(applyMethod);
        provider.setTarget(multipleArgumentsFunction);
        provider.setName(multipleArgumentsFunction.getClass().getName());
        repository.put(provider);
        return provider;
    }
}
