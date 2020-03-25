package com.igniubi.core.aysnc.model;

import lombok.Data;

import java.util.Map;

@Data
public class AysncConsumerDefinition {
    private String name;
    private Class<?> clazz;
    private Boolean ignoreException;
    private Map<String, String> dynamicParameterKeyMap;
    private String originalParameterName;
}
