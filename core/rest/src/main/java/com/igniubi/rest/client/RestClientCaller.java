package com.igniubi.rest.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;


@Component
public class RestClientCaller implements IRestClient {

    @Autowired
    WebClientUtil clientUtil;


    @Override
    public <T> T post(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        return clientUtil.postEntity(url, request, responseType);
    }

    @Override
    public <T> T call(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        Mono<T> mono = clientUtil.post(url, request, responseType);
        return new AsyncResult<>(mono.toFuture(), url).get();
    }

    @Override
    public <T> T call(String serviceName, String serviceUrl, Object request, Class<T> responseType, int timeout) {
        String url = serviceName + serviceUrl;
        Mono<T> mono = clientUtil.post(url, request, responseType);
        return new AsyncResult<>(mono.toFuture(), url).get(timeout, TimeUnit.SECONDS);
    }

    @Override
    public <T> T get(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        Mono<T> mono = clientUtil.get(url, request, responseType);
        return new AsyncResult<>(mono.toFuture(), url).get();
    }

    @Override
    public <T> AsyncResult<T> asyncCall(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        Mono<T> mono = clientUtil.post(url, request, responseType);
        return new AsyncResult<>(mono.toFuture(), url);
    }

    @Override
    public <T> AsyncResult<T> asyncGet(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        Mono<T> mono = clientUtil.get(url, request, responseType);
        return new AsyncResult<>(mono.toFuture(), url);
    }
}
