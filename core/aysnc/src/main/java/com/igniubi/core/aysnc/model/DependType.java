package com.igniubi.core.aysnc.model;

/**
 */
public enum DependType {
    /**
     * Caller passed parameters
     */
    INVOKE_PARAM,
    /**
     * Parameters that require automatic injection
     */
    OTHER_MODEL
}
