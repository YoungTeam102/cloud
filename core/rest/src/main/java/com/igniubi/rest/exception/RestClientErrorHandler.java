package com.igniubi.rest.exception;

import com.igniubi.common.exceptions.IGNBException;
import com.igniubi.common.exceptions.IGNBGlobalExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;


/**
 * RestTemplate异常处理，下面的情况认为是出错了：
 * 1. 调用服务返回不是200 OK 则抛出 {@link HttpClientErrorException} 或者 {@link HttpServerErrorException}
 * 2. 调用服务返回200 OK，但是响应头中有一个自定义的:x-yh-service-error-code, ，则抛出 {@link IGNBException}
 */
public class RestClientErrorHandler implements ResponseErrorHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = getHttpStatusCode(response);
        switch (statusCode.series()) {
            case CLIENT_ERROR:{
                HttpClientErrorException ex = new HttpClientErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response));
                log.error("service call client error",  ex);
                throw ex;
            }

            case SERVER_ERROR: {
                HttpServerErrorException ex = new HttpServerErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response));
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

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {

        int errorCode = this.getServiceErrorCode(response);

        HttpStatus statusCode= getHttpStatusCode(response);
        return  (errorCode>0) || (statusCode.series() == HttpStatus.Series.CLIENT_ERROR ||
                statusCode.series() == HttpStatus.Series.SERVER_ERROR) ;
    }

    private int getServiceErrorCode(ClientHttpResponse response){
        String header =  response.getHeaders().getFirst(IGNBGlobalExceptionHandler.HEADER_ERROR_CODE);
        if(StringUtils.isNotEmpty(header)){
            return Integer.parseInt(header);
        }
        return 0;
    }

    private String getServiceErrorMessage(ClientHttpResponse response){
        String header =  response.getHeaders().getFirst(IGNBGlobalExceptionHandler.HEADER_ERROR_MESSAGE);
        if(StringUtils.isNotEmpty(header)){
            try {
                header = new String(header.getBytes("ISO8859-1"),"UTF-8");
                return header;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode;
        try {
            statusCode = response.getStatusCode();
        }
        catch (IllegalArgumentException ex) {
            throw new UnknownHttpStatusCodeException(response.getRawStatusCode(),
                    response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
        }
        return statusCode;
    }

    private byte[] getResponseBody(ClientHttpResponse response) {
        try {
            InputStream responseBody = response.getBody();
            return FileCopyUtils.copyToByteArray(responseBody);
        }
        catch (IOException ex) {
            // ignore
        }
        return new byte[0];
    }

    private Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return contentType != null ? contentType.getCharset() : null;
    }



}