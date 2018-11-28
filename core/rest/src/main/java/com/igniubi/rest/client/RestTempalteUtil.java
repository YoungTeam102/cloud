package com.igniubi.rest.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Component
public class RestTempalteUtil {


    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final String FORM_WITH_UTF8 = "application/x-www-form-urlencoded;charset=utf-8";
    protected static final Logger log = LoggerFactory.getLogger(RestTempalteUtil.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AsyncRestTemplate asyncRestTemplate;

    public  <T> T post(String url, Object request, Class<T> responseType){
        MultiValueMap<String, String> headers = setupPostHeaders(request);
        HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
        T t=  restTemplate.postForObject(url , requestEntity ,responseType);

        return t;
    }

    public <T> ListenableFuture<ResponseEntity<T>>  asyncPost(String serviceName, String serviceUrl, Object request, Class<T> responseType){
        String url = serviceName + serviceUrl;
        MultiValueMap<String, String> headers = setupPostHeaders(request);
        HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);
        ListenableFuture<ResponseEntity<T>> t=  asyncRestTemplate.postForEntity(url , requestEntity ,responseType, new HashMap<>());

        return t;
    }


    public <T> T get(String path, Object request, Class<T> responseType)   {

        log.debug("begin to do http get. path:{}, request params:{}", path, request);

        //add accept json
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);


        HttpEntity<?> entity = new HttpEntity<>(headers);

        //url
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(path);

        //添加参数
        Map<String, ?> params = (Map<String, ?>) request;
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, ?> urlParam : params.entrySet()) {
                builder.queryParam(urlParam.getKey(), urlParam.getValue());
            }
        }
        final URI getURI = builder.build().encode().toUri();

        HttpEntity<T> response = restTemplate.exchange(getURI, HttpMethod.GET, entity, responseType);

        if (log.isDebugEnabled()) {
            log.debug("end to do http get. path:{}, request params:{}. response body: {}", path, request,response.getBody());
        }
        return response.getBody();
    }

    public <T> ListenableFuture<ResponseEntity<T>> asyncGet(String path, Object request, Class<T> responseType)   {

        log.debug("begin to do http get. path:{}, request params:{}", path, request);

        //add accept json
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);


        HttpEntity<?> entity = new HttpEntity<>(headers);

        //url
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(path);

        //添加参数
        Map<String, ?> params = (Map<String, ?>) request;
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, ?> urlParam : params.entrySet()) {
                builder.queryParam(urlParam.getKey(), urlParam.getValue());
            }
        }
        final URI getURI = builder.build().encode().toUri();

        ListenableFuture<ResponseEntity<T>> t= asyncRestTemplate.exchange(getURI, HttpMethod.GET, entity, responseType);

        if (log.isDebugEnabled()) {
            log.debug("end to do http get. path:{}, request params:{}. response body: {}", path, request,t);
        }
        return t;
    }
    /**
     *  设置post的请求头。 如果request是MultiValueMap，则使用form方式提交，并且设置content-type为utf8
     * @param request 请求
     * @return post头
     */
    private static MultiValueMap<String, String> setupPostHeaders(Object request) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap();
        if (request != null && MultiValueMap.class.isAssignableFrom(request.getClass())) {
            headers.set("Content-Type", FORM_WITH_UTF8);
        }else {
                headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        }
        return headers;
    }
}
