package com.igniubi.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p/>
 */
public class AsyncFuture<T> {
    private final static Logger log = LoggerFactory.getLogger(AsyncFuture.class);


    private final Future<T> future;
    private final String serviceName;
    private final int timeOut = 5;

    /**
     *  constructor
     */
    public AsyncFuture(Future<T> future, String serviceName) {
        this.future = future;
        this.serviceName = serviceName;
    }


    /**
     * 获取服务调用结果
     *
     * @return 获取服务调用结果
     */
    public T get(int seconds) {
        T t = null;
        try {
            t = this.innerGet(TimeUnit.SECONDS.toMillis(seconds),TimeUnit.MILLISECONDS);
        } catch (Exception e) {
        } finally {
        }

        return t;

    }
    
    
    public T get(int seconds,TimeUnit unit) {

        T t = null;
        try {
            t = this.innerGet(unit.toMillis(seconds),TimeUnit.MILLISECONDS);
        } catch (Exception e) {
           
        }
        return t;
    }
    
    
    /**
     * 获取服务调用结果
     *
     * @return 获取服务调用结果
     */
    private T innerGet(long waitingSeconds,TimeUnit unit) {
        T t = null;
        try {
            t = this.future.get(waitingSeconds, unit);
            
            //避免LogUtils.shotter  先执行
        }catch(TimeoutException te){//增加超时异常
        	log.warn("call service: {} failed:{} with TimeoutException.", serviceName,te);
            this.future.cancel(true);
        }catch(InterruptedException ie){//增加线程中断异常
        	log.warn("call service: {} failed:{} with InterruptedException.", serviceName,ie);
        } catch (Exception ex) {
        }
        return t;
    }




    /**
     * 获取服务调用结果
     *
     * @return 获取服务调用结果
     */
    public T get() {
        final int defaultTimeout=1000;
        return this.get(defaultTimeout,TimeUnit.MILLISECONDS);
    }


}
