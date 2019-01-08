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
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

public class WebClientErrorHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private ClientResponse response;


    public WebClientErrorHandler(ClientResponse response) {
        this.response = response;
    }

    public <T> Mono<T> handleError()  {
        HttpStatus statusCode = null;
        try {
            statusCode = getHttpStatusCode(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private int getServiceErrorCode(ClientResponse response){
        List<String> header =  response.headers().header(IGNBGlobalExceptionHandler.HEADER_ERROR_CODE);
        if(header.size()>0 ){
            return Integer.parseInt(header.get(0));
        }
        return 0;
    }

    public boolean hasError(ClientResponse response ) throws IOException {

        int errorCode = this.getServiceErrorCode(response);

        HttpStatus statusCode= getHttpStatusCode(response);
        return  (errorCode>0) || (statusCode.series() == HttpStatus.Series.CLIENT_ERROR ||
                statusCode.series() == HttpStatus.Series.SERVER_ERROR) ;
    }

    private String getServiceErrorMessage(ClientResponse response){
        List<String> header =  response.headers().header(IGNBGlobalExceptionHandler.HEADER_ERROR_MESSAGE);
        if(header.size()>0 ){
            try {
                String header1 = new String(header.get(0).getBytes("ISO8859-1"),"UTF-8");
                return header1;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private HttpStatus getHttpStatusCode(ClientResponse response) throws IOException {
        HttpStatus statusCode;
        try {
            statusCode = response.statusCode();
        }
        catch (IllegalArgumentException ex) {
            throw new UnknownHttpStatusCodeException(response.rawStatusCode(),
                    response.statusCode().getReasonPhrase(),  response.headers().asHttpHeaders(), getResponseBody(response), getCharset(response));
        }
        return statusCode;
    }

    private byte[] getResponseBody(ClientResponse response) {
        Flux<byte[]> responseBody = response.bodyToFlux(byte[].class);
        return responseBody.blockFirst();
    }

    private Charset getCharset(ClientResponse response) {
        ClientResponse.Headers headers = response.headers();
        Optional<MediaType> contentType = headers.contentType();
        return contentType.map(MimeType::getCharset).orElse(null);
    }

}
