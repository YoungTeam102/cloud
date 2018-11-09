package com.igniubi.rest.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestServiceCaller implements IRestClient{

    @Autowired
    RestTempalteUtil tempalteUtil;

    @Override
    public <T> T call(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        return tempalteUtil.post(url, request ,responseType);
    }

    @Override
    public <T> T get(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        String url = serviceName + serviceUrl;
        return tempalteUtil.get(url, request ,responseType);
    }

    @Override
    public <T> AsyncFuture<T> asyncCall(String serviceName, String serviceUrl, Object request, Class<T> responseType) {
        return null;
    }
}
