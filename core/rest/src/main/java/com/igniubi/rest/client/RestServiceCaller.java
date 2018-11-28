package com.igniubi.rest.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

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
        ListenableFuture<ResponseEntity<T>> t = tempalteUtil.asyncPost(serviceName,serviceUrl , request , responseType);
        return new AsyncFuture<T>(t, serviceName+serviceUrl);
    }
}
