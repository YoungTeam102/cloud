package com.igniubi.core.aysnc.utils;

import com.igniubi.core.aysnc.annotation.AysncConsumer;
import com.igniubi.core.aysnc.annotation.DynamicParameter;
import com.igniubi.core.aysnc.annotation.InvokeParameter;
import com.igniubi.core.aysnc.model.*;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefinitionUtils {

    /**
     * get provider's consume definitions
     *
     * @param method provider method
     * @return result
     */
    public static AysncProviderDefinition getProvideDefinition(Method method){
        Parameter[] parameters = method.getParameters();

        AysncProviderDefinition provider = new AysncProviderDefinition();
        List<MethodArg> methodArgs = new ArrayList<>(method.getParameterCount());
        provider.setDepends(new ArrayList<>(method.getParameterCount()));
        provider.setParams(new ArrayList<>(method.getParameterCount()));
        provider.setMethod(method);

        for (Parameter parameter : parameters) {
            dealMethodParameter(provider, methodArgs, parameter);
        }
        provider.setMethodArgs(methodArgs);
        return provider;
    }

    private static void dealMethodParameter(AysncProviderDefinition provideDefinition,
                                            List<MethodArg> methodArgs, Parameter parameter) {
        AysncConsumer dataConsumer = AnnotationUtils.findAnnotation(parameter, AysncConsumer.class);
        InvokeParameter invokeParameter = AnnotationUtils.findAnnotation(parameter,InvokeParameter.class);
        Assert.isTrue(dataConsumer != null || invokeParameter != null,
                "Parameters must be added @InvokeParameter or @AysncConsumer annotation");
        MethodArg methodArg = new MethodArg();
        if(dataConsumer != null) {
            String dataId = dataConsumer.name();
            Assert.isTrue(! StringUtils.isEmpty(dataId),"data name must be not null!");
            methodArg.setAnnotationKey(dataId);
            methodArg.setDependType(DependType.OTHER_MODEL);
            AysncConsumerDefinition dataConsumeDefinition = new AysncConsumerDefinition();
            dataConsumeDefinition.setClazz(parameter.getType());
            dataConsumeDefinition.setName(dataId);
            if(dataConsumer.dynamicParameters().length > 0) {
                Map<String, String> parameterKeyMap = new HashMap<>(dataConsumer.dynamicParameters().length);
                for (DynamicParameter dynamicParameter : dataConsumer.dynamicParameters()) {
                    parameterKeyMap.put(dynamicParameter.targetKey(),dynamicParameter.replacementKey());
                }
                dataConsumeDefinition.setDynamicParameterKeyMap(parameterKeyMap);
            }
            dataConsumeDefinition.setOriginalParameterName(parameter.getName());
//            if(! dataConsumer.exceptionProcessingMethod().equals(ExceptionProcessingMethod.BY_DEFAULT)) {
//                dataConsumeDefinition.setIgnoreException(
//                        dataConsumer.exceptionProcessingMethod().equals(ExceptionProcessingMethod.IGNORE)
//                );
//            }
            provideDefinition.getDepends().add(dataConsumeDefinition);
        } else {
            methodArg.setAnnotationKey(invokeParameter.value());
            methodArg.setDependType(DependType.INVOKE_PARAM);
            InvokeParameterDefinition parameterDefinition = new InvokeParameterDefinition();
            parameterDefinition.setKey(invokeParameter.value());
            provideDefinition.getParams().add(parameterDefinition);
        }
        methodArg.setParameter(parameter);
        methodArgs.add(methodArg);
    }
}
