package com.igniubi.core.aysnc.model;

import lombok.Data;

import java.lang.reflect.Parameter;

/**
 */
@Data
public class MethodArg {
    private String     annotationKey;
    private DependType dependType;
    private Parameter  parameter;
}
