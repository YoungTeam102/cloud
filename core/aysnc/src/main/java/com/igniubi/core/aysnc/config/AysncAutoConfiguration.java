package com.igniubi.core.aysnc.config;

import com.igniubi.core.aysnc.annotation.AysncProvider;
import com.igniubi.core.aysnc.facade.AysncBeanQueryFacade;
import com.igniubi.core.aysnc.facade.AysncDataFacade;
import com.igniubi.core.aysnc.facade.DefaultAysncBeanQueryFacade;
import com.igniubi.core.aysnc.model.AysncConsumerDefinition;
import com.igniubi.core.aysnc.model.AysncProviderDefinition;
import com.igniubi.core.aysnc.repository.AysncProviderRepository;
import com.igniubi.core.aysnc.repository.DefaultAysncProviderRepository;
import com.igniubi.core.aysnc.service.AysncDataQueryService;
import com.igniubi.core.aysnc.service.DefaultAysncDataQueryService;
import com.igniubi.core.aysnc.utils.DefinitionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(AysncProperties.class)
public class AysncAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private AysncProperties properties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 允许自定义线程池
     *
     * @return 线程池服务
     */
    @Bean(name = "aysncExecutorService")
    @ConditionalOnMissingBean(name = "aysncExecutorService",value=ExecutorService.class)
    public ExecutorService aggregateExecutorService() {
        return new ThreadPoolExecutor(
                properties.getThreadNumber(),
                properties.getThreadNumber() ,
                2L, TimeUnit.HOURS,
                new LinkedBlockingDeque<>(properties.getQueueSize()),
                new CustomizableThreadFactory(properties.getThreadPrefix()));
    }

    /**
     * 允许用户自定义provider存储
     *
     * @return  provider数据仓库
     */
    @Bean(name = "aysncProviderRepository")
    @ConditionalOnMissingBean(AysncProviderRepository.class)
    public AysncProviderRepository dataProviderRepository() {
        return new DefaultAysncProviderRepository();
    }


    @Bean
    @ConditionalOnMissingBean
    public AysncDataQueryService dataBeanAggregateQueryService (
            @Qualifier("aysncProviderRepository") AysncProviderRepository aysncProviderRepository) {
        if(properties.getBasePackages() != null) {
            Map<String,Set<String>> provideDependMap = new HashMap<>(64);
            for (String basePackage : properties.getBasePackages()) {
                Reflections reflections = new Reflections(basePackage, new MethodAnnotationsScanner());
                Set<Method> providerMethods = reflections.getMethodsAnnotatedWith(AysncProvider.class);
                for (Method method : providerMethods) {
                    AysncProvider beanProvider = AnnotationUtils.findAnnotation(method, AysncProvider.class);
                    String dataId = beanProvider.name();
                    Assert.isTrue(Modifier.isPublic(method.getModifiers()),"data provider method must be public");
                    Assert.isTrue(! StringUtils.isEmpty(dataId),"data id must be not null!");
                    AysncProviderDefinition provider = DefinitionUtils.getProvideDefinition(method);

                    provider.setName(dataId);
                    provider.setIdempotent(beanProvider.idempotent());
                    provider.setTimeout(beanProvider.timeout() > 0 ? beanProvider.timeout() : properties.getDefaultTimeout());
                    Assert.isTrue(! aysncProviderRepository.contains(dataId), "Data providers with the same name are not allowed. dataId: " + dataId);
                    provideDependMap.put(dataId, provider.getDepends().stream().map(AysncConsumerDefinition::getName).collect(Collectors.toSet()));
                    aysncProviderRepository.put(provider);
                }
            }
            checkCycle(provideDependMap);
        }

        DefaultAysncDataQueryService service = new DefaultAysncDataQueryService();
        RuntimeSettings runtimeSettings = new RuntimeSettings();
        runtimeSettings.setIgnoreException(properties.isIgnoreException());
        runtimeSettings.setTimeout(properties.getDefaultTimeout());
        service.setRepository(aysncProviderRepository);
        service.setRuntimeSettings(runtimeSettings);
        service.setExecutorService(aggregateExecutorService());
        service.setApplicationContext(applicationContext);
        return service;
    }


    /**
     * 检查是不是有循环依赖
     * @param graphAdjMap
     */
    private void checkCycle(Map<String,Set<String>> graphAdjMap) {
        Map<String,Integer> visitStatusMap = new HashMap<>(graphAdjMap.size() * 2);
        for (Map.Entry<String, Set<String>> item : graphAdjMap.entrySet()) {
            if (visitStatusMap.containsKey(item.getKey())) {
                continue;
            }
            dfs(graphAdjMap,visitStatusMap,item.getKey());
        }
    }

    private void dfs(Map<String,Set<String>> graphAdjMap,Map<String,Integer> visitStatusMap, String node) {
        if (visitStatusMap.containsKey(node)) {
            if(visitStatusMap.get(node) == 1) {
                List<String> relatedNodes = new ArrayList<>();
                for (Map.Entry<String,Integer> item : visitStatusMap.entrySet()) {
                    if (item.getValue() == 1) {
                        relatedNodes.add(item.getKey());
                    }
                }
                throw new IllegalStateException("There are loops in the dependency graph. Related nodes:" + StringUtils.join(relatedNodes));
            }
            return ;
        }
        visitStatusMap.put(node,1);
        for (String relateNode : graphAdjMap.get(node)) {
            dfs(graphAdjMap,visitStatusMap,relateNode);
        }
        visitStatusMap.put(node,2);
    }

    @Bean
    @ConditionalOnMissingBean
    public AysncDataFacade dataBeanAggregateQueryFacade(
            @Qualifier("aysncProviderRepository") AysncProviderRepository aysncProviderRepository) {
        AysncBeanQueryFacade facade = new DefaultAysncBeanQueryFacade();
        ((DefaultAysncBeanQueryFacade) facade).setAysncDataQueryService(dataBeanAggregateQueryService(aysncProviderRepository));
        return new AysncDataFacade(facade);
    }
}
