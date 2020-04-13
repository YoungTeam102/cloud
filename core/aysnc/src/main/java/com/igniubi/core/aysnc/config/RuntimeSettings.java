package com.igniubi.core.aysnc.config;

import lombok.Data;

@Data
public class RuntimeSettings {
    private boolean ignoreException;
    private Long timeout;
}
