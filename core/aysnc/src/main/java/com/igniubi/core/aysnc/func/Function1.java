package com.igniubi.core.aysnc.func;

@FunctionalInterface
public interface Function1<R> extends MultipleArgumentsFunction<R> {
    
    R apply();
}
