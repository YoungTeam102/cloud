package com.igniubi.core.aysnc.model;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

@Data
public class AysncProviderDefinition {
    private String name;
    private Method method;
    private Object target;
    private Long timeout;
    private List<AysncConsumerDefinition> depends;
    private List<InvokeParameterDefinition> params;
    private List<MethodArg> methodArgs;
    private boolean idempotent;
    private boolean useCache;

}
