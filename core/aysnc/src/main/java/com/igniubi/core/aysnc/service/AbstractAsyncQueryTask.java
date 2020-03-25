package com.igniubi.core.aysnc.service;

import java.util.concurrent.Callable;

public abstract class AbstractAsyncQueryTask<T> implements Callable<T> {

    /**
     * 任务来源线程
     */
    private Thread taskFromThread;
    /**
     * 异步任务包装器
     */

    protected AbstractAsyncQueryTask(Thread taskFromThread) {
        this.taskFromThread = taskFromThread;
    }

    @Override
    public T call() throws Exception {
        return execute();
    }

    /**
     * 异步任务的实际内容
     *
     * @return 异步任务返回值
     * @throws Exception 异步任务允许抛出异常
     */
    public abstract T execute() throws Exception;
}
