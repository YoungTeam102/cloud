package com.igniubi.rest.client;

import com.igniubi.rest.Async.AsyncRestCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.concurrent.Future;

@Component
public class RestClientCaller implements IRestClient {

    @Autowired
    WebClientUtil clientUtil;


    @Override
    public <T> T post(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        T t = clientUtil.postEntity(url, request, responseType);
        return t;
    }

    @Override
    public <T> T call(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        Mono<T> mono  = clientUtil.post(url, request, responseType);
        return new AsyncFuture<T>(mono.toFuture(), url).get();
    }

    @Override
    public <T> T get(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        Mono<T> mono = clientUtil.get(url, request, responseType);
        return new AsyncFuture<T>(mono.toFuture(), url).get();
    }

    @Override
    public <T> AsyncFuture<T> asyncCall(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        Mono<T> mono  = clientUtil.post(url, request, responseType);
        return new AsyncFuture<T>(mono.toFuture(), url);
    }

    @Override
    public <T> AsyncFuture<T> asyncGet(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        Mono<T> mono = clientUtil.get(url, request, responseType);
        return new AsyncFuture<T>(mono.toFuture(), url);
    }
}
