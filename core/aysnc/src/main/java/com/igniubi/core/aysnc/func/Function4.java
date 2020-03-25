package com.igniubi.core.aysnc.func;


@FunctionalInterface
public interface Function4<T,U,V,W,R> extends MultipleArgumentsFunction<R> {
    /**
     * support four parameters
     *
     * @param t param 1
     * @param u param 2
     * @param v param 3
     * @param w param 4
     * @return return value
     */
    R apply(T t, U u, V v, W w);
}
