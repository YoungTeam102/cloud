package com.igniubi.rest.Async;

import com.igniubi.rest.client.RestTempalteUtil;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;

public class AsyncRestCommand<T> {

    private ExecutorService executor = Executors.newFixedThreadPool(10, Executors.defaultThreadFactory()) ;


    private final RestTemplate restTemplate;
    private final String url;
    private final Object request;
    private final Class<T> responseType;

    public AsyncRestCommand(RestTemplate restTemplate,String url, Object request, Class<T> responseType) {
        this.url = url;
        this.request = request;
        this.responseType = responseType;
        this.restTemplate = restTemplate;
    }

    public Future<T> excute(){
        Future<T> t= executor.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return RestTempalteUtil.post(restTemplate,url, request, responseType);
            }
        });
        return t;
    }

    public Future<T> excuteGet(){
        Future<T> t= executor.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return RestTempalteUtil.get(restTemplate,url, request, responseType);
            }
        });
        return t;
    }

}
