package com.igniubi.rest.client;

import com.igniubi.common.exceptions.IGNBException;
import com.igniubi.model.enums.common.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.concurrent.ExecutionException;
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
     * constructor
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

        t = this.innerGet(TimeUnit.SECONDS.toMillis(seconds), TimeUnit.MILLISECONDS);


        return t;

    }


    public T get(int seconds, TimeUnit unit) {

        T t = null;
        t = this.innerGet(unit.toMillis(seconds), TimeUnit.MILLISECONDS);

        return t;
    }


    /**
     * 获取服务调用结果
     *
     * @return 获取服务调用结果
     */
    private T innerGet(long waitingSeconds, TimeUnit unit) {
        T t = null;
        try {
                t = this.future.get(waitingSeconds, unit);

            //避免LogUtils.shotter  先执行
        } catch (TimeoutException te) {//增加超时异常
            log.warn("call service: {} failed:{} with TimeoutException.", serviceName, te);
            this.future.cancel(true);
            throw new IGNBException(ResultEnum.SERVICE_NOT_AVAILABLE);
        } catch (InterruptedException ie) {//增加线程中断异常
            log.warn("call service: {} failed:{} with InterruptedException.", serviceName, ie);
            throw new IGNBException(ResultEnum.SERVICE_NOT_AVAILABLE);
        } catch (Exception ex) {
            if (ex instanceof ExecutionException) {
                if (ex.getCause() instanceof IGNBException ) {
                    IGNBException se = (IGNBException) (ex.getCause());
                    log.info("call service: {}, return: {} success.", serviceName, se.getMessage());
                    throw se;
                }
                //不是service exception
                else {
                    log.warn("call service: {} failed with exception.", serviceName, ex);
                    throw new IGNBException(ResultEnum.SERVICE_NOT_AVAILABLE);
                }
            }
            else {
                log.warn("call service: {} failed with exception.", serviceName, ex);
                throw new IGNBException(ResultEnum.SERVICE_NOT_AVAILABLE);
            }
        }
        return t;
    }


    /**
     * 获取服务调用结果
     *
     * @return 获取服务调用结果
     */
    public T get() {
        final int defaultTimeout = 5000;
        return this.get(defaultTimeout, TimeUnit.MILLISECONDS);
    }


}
