package com.igniubi.rest.client;

import com.igniubi.rest.Async.AsyncRestCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
@Deprecated
public class RestServiceCaller implements IRestClient {

    @Autowired
    RestTempalteUtil tempalteUtil;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public <T> T post(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        T t = tempalteUtil.post(url, request, responseType);
        return t;
    }

    @Override
    public <T> T call(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        AsyncRestCommand<T> command = new AsyncRestCommand<>(restTemplate, url, request, responseType);
        Future<T> t = command.execute();
        return new AsyncResult<T>(t, url).get();
    }

    @Override
    public <T> T call(String serviceName, String serviceUrl, Object request, Class<T> responseType, int timeout) {
        String url = serviceName + serviceUrl;
        AsyncRestCommand<T> command = new AsyncRestCommand<>(restTemplate, url, request, responseType);
        Future<T> t = command.execute();
        return new AsyncResult<T>(t, url).get(timeout, TimeUnit.SECONDS);
    }

    @Override
    public <T> T get(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        AsyncRestCommand<T> command = new AsyncRestCommand<>(restTemplate, url, request, responseType);
        Future<T> t = command.executeGet();
        return new AsyncResult<T>(t, url).get();
    }

    @Override
    public <T> AsyncResult<T> asyncCall(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        AsyncRestCommand<T> command = new AsyncRestCommand<>(restTemplate, url, request, responseType);
        Future<T> t = command.execute();
        return new AsyncResult<T>(t, url);
    }

    @Override
    public <T> AsyncResult<T> asyncGet(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        AsyncRestCommand<T> command = new AsyncRestCommand<>(restTemplate, url, request, responseType);
        Future<T> t = command.executeGet();
        return new AsyncResult<T>(t, url);
    }
}
