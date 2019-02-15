package com.igniubi.rest.exception;

import com.igniubi.common.exceptions.IGNBException;
import com.igniubi.common.exceptions.IGNBGlobalExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
/**
 * webclient异常处理，下面的情况认为是出错了：
 * 1. 调用服务返回不是200 ，响应头中有一个自定义的:x-service-error-code, ，则抛出 {@link IGNBException}
 *  2.调用服务返回不是200 ,响应头中没有x-service-error-code 则抛出 {@link HttpClientErrorException} 或者 {@link HttpServerErrorException}
 */
public class WebClientErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(WebClientErrorHandler.class);

    private ClientResponse response;




    public static  <T> Mono<T> handleError(ClientResponse response)  {
        HttpStatus statusCode = getHttpStatusCode(response);
        assert statusCode != null;
        switch (statusCode.series()) {
            case CLIENT_ERROR:{
                if(getServiceErrorCode(response) > 0){
                    IGNBException se =  new IGNBException(getServiceErrorCode(response), getServiceErrorMessage(response));
                    log.info("service call with service exception: {}",  se.getMessage());
                    throw  se;
                }
                HttpClientErrorException ex = new HttpClientErrorException(statusCode, response.statusCode().getReasonPhrase(),
                        response.headers().asHttpHeaders(), getResponseBody(response), getCharset(response));
                log.error("service call client error",  ex);
                throw ex;
            }

            case SERVER_ERROR: {
                if(getServiceErrorCode(response) > 0){
                    IGNBException se =  new IGNBException(getServiceErrorCode(response), getServiceErrorMessage(response));
                    log.info("service call with service exception: {}",  se.getMessage());
                    throw  se;
                }
                HttpServerErrorException ex = new HttpServerErrorException(statusCode, response.statusCode().getReasonPhrase(),
                        response.headers().asHttpHeaders(), getResponseBody(response), getCharset(response));
                log.error("service call server error",  ex);
                throw ex;
            }

            default: {
                IGNBException se =  new IGNBException(getServiceErrorCode(response), getServiceErrorMessage(response));
                log.info("service call with service exception: {}",  se.getMessage());
                throw  se;
            }
        }

    }

    private static  int getServiceErrorCode(ClientResponse response){
        List<String> header =  response.headers().header(IGNBGlobalExceptionHandler.HEADER_ERROR_CODE);
        if(header.size()>0 ){
            return Integer.parseInt(header.get(0));
        }
        return 0;
    }


    private static String getServiceErrorMessage(ClientResponse response){
        List<String> header =  response.headers().header(IGNBGlobalExceptionHandler.HEADER_ERROR_MESSAGE);
        if(header.size()>0 ){
            try {
                return new String(header.get(0).getBytes("ISO8859-1"),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private static HttpStatus getHttpStatusCode(ClientResponse response)   {
        HttpStatus statusCode;
        statusCode = response.statusCode();
        return statusCode;
    }

    private static byte[] getResponseBody(ClientResponse response) {
        Flux<byte[]> responseBody = response.bodyToFlux(byte[].class);
        return responseBody.blockFirst();
    }

    private static Charset getCharset(ClientResponse response) {
        ClientResponse.Headers headers = response.headers();
        Optional<MediaType> contentType = headers.contentType();
        return contentType.map(MimeType::getCharset).orElse(null);
    }

}
