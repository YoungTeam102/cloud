package com.igniubi.rest.client;


public interface IRestClient {

    <T> T post(String serviceName, String serviceUrl, Object request, Class<T> responseType);

    <T> T call(String serviceName, String serviceUrl, Object request, Class<T> responseType);

    <T> T call(String serviceName, String serviceUrl, Object request, Class<T> responseType, int timeout);

    <T> T get(String serviceName, String serviceUrl, Object request, Class<T> responseType);

    <T> AsyncResult<T> asyncCall(String serviceName, String serviceUrl, Object request, Class<T> responseType);

    <T> AsyncResult<T> asyncGet(String serviceName, String serviceUrl, Object request, Class<T> responseType);
}
