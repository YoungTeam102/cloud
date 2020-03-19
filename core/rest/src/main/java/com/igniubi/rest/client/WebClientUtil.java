package com.igniubi.rest.client;

import com.igniubi.rest.exception.WebClientErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
public class WebClientUtil {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public <T> Mono<T> get(String url, Object request, Class<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        Map<String, ?> params = (Map<String, ?>) request;
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, ?> urlParam : params.entrySet()) {
                builder.queryParam(urlParam.getKey(), urlParam.getValue());
            }
        }
        final URI getURI = builder.build().encode().toUri();
        Mono<T> mono = webClientBuilder.build().get().uri(getURI).retrieve().onStatus(HttpStatus::isError, WebClientErrorHandler::handleError).bodyToMono(responseType);
        return mono;
    }

    public <T> T getEntity(String url, Object request, Class<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        Map<String, ?> params = (Map<String, ?>) request;
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, ?> urlParam : params.entrySet()) {
                builder.queryParam(urlParam.getKey(), urlParam.getValue());
            }
        }
        final URI getURI = builder.build().encode().toUri();
        Mono<T> mono = webClientBuilder.build().get().uri(getURI).retrieve().onStatus(HttpStatus::isError, WebClientErrorHandler::handleError).bodyToMono(responseType);
        return mono.block();
    }

    public <T> Mono<T> post(String url, Object request, Class<T> responseType) {
        Mono<T> mono = webClientBuilder.build().post().uri(url).syncBody(request).retrieve().onStatus(HttpStatus::isError, WebClientErrorHandler::handleError).bodyToMono(responseType);
        return mono;
    }

    public <T> T postEntity(String url, Object request, Class<T> responseType) {
        Mono<T> mono = webClientBuilder.build().post().uri(url).syncBody(request).retrieve().onStatus(HttpStatus::isError, WebClientErrorHandler::handleError).bodyToMono(responseType);
        return mono.block();
    }
}
