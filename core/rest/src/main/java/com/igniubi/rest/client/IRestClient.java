package com.igniubi.rest.client;


import java.util.concurrent.ExecutionException;

public interface IRestClient {

    <T> T call(String serviceName, String serviceUrl, Object request, Class<T> responseType) throws ExecutionException, InterruptedException;

    <T> T get(String serviceName, String serviceUrl, Object request, Class<T> responseType);

    <T> AsyncFuture<T> asyncCall(String serviceName, String serviceUrl, Object request, Class<T> responseType);

    <T> AsyncFuture<T> asyncGet(String serviceName, String serviceUrl, Object request, Class<T> responseType);
}
