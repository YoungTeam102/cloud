package com.igniubi.core.aysnc.func;


@FunctionalInterface
public interface Function2<T,U,R> extends MultipleArgumentsFunction<R> {
    /**
     * support two parameters
     *
     * @param t param 1
     * @param u param 2
     * @return return value
     */
    R apply(T t, U u);
}
