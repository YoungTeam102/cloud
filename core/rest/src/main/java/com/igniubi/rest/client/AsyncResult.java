package com.igniubi.rest.client;

import com.igniubi.common.exceptions.IGNBException;
import com.igniubi.model.enums.common.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class AsyncResult<T> {
    private final static Logger log = LoggerFactory.getLogger(AsyncResult.class);
    private final Future<T> future;
    private final String serviceName;

    /**
     * constructor
     */
    public AsyncResult(Future<T> future, String serviceName) {
        this.future = future;
        this.serviceName = serviceName;
    }

    public T get(int seconds) {
        T t;
        t = this.innerGet(TimeUnit.SECONDS.toMillis(seconds));
        return t;

    }

    public T get(int seconds, TimeUnit unit) {
        T t;
        t = this.innerGet(unit.toMillis(seconds));
        return t;
    }


    private T innerGet(long waitingSeconds) {
        T t;
        try {
            t = this.future.get(waitingSeconds, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {//增加超时异常
            log.warn("call service: {} failed:{} with TimeoutException.", serviceName, te);
            this.future.cancel(true);
            throw new IGNBException(ResultEnum.SERVICE_NOT_AVAILABLE);
        } catch (InterruptedException ie) {//增加线程中断异常
            log.warn("call service: {} failed:{} with InterruptedException.", serviceName, ie);
            throw new IGNBException(ResultEnum.SERVICE_NOT_AVAILABLE);
        } catch (Exception ex) {
            if (ex instanceof ExecutionException) {
                if (ex.getCause() instanceof IGNBException) {
                    IGNBException se = (IGNBException) (ex.getCause());
                    log.info("call service: {}, return: {} success.", serviceName, se.getMessage());
                    throw se;
                }
                //不是service exception
                else {
                    log.warn("call service: {} failed with exception.", serviceName, ex);
                    throw new IGNBException(ResultEnum.SERVICE_NOT_AVAILABLE);
                }
            } else {
                log.warn("call service: {} failed with exception.", serviceName, ex);
                throw new IGNBException(ResultEnum.SERVICE_NOT_AVAILABLE);
            }
        }
        return t;
    }


    public T get() {
        final int defaultTimeout = 5000;
        return this.get(defaultTimeout, TimeUnit.MILLISECONDS);
    }


}
