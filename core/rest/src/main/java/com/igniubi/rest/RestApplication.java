package com.igniubi.rest;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.igniubi.rest.exception.RestClientErrorHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.http.converter.*;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static io.netty.channel.ChannelOption.TCP_NODELAY;

@SpringBootApplication
@ComponentScan(basePackages = "com.igniubi.rest")
@EnableHystrix
public class RestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }


    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        //转化器顺序有变动可能导致 Can not deserialize instance of java.lang.String out of START_ARRAY token
        //Could not read document: Can not deserialize instance of java.lang.String out of START_OBJECT token
        //可以根据mediaType指定每个转化器需要的类型
        //部分转化器默认匹配全部的MediaType  详见restTemplate的doWithRequest（）方法和转化器的 canRead（）/canWrite（）方
//法
        converters.add(new ByteArrayHttpMessageConverter());
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.parseMediaType(MediaType.TEXT_HTML_VALUE));
        mediaTypes.add(MediaType.parseMediaType(MediaType.TEXT_PLAIN_VALUE));
        mediaTypes.add(MediaType.parseMediaType(MediaType.TEXT_XML_VALUE));

        StringHttpMessageConverter stringHttpMessageConverter =  new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        converters.add(stringHttpMessageConverter);
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new FormHttpMessageConverter());

        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        List<MediaType> mediaTypes1 = new ArrayList<>();
        mediaTypes1.add(MediaType.APPLICATION_JSON_UTF8);
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
        fastJsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes1);
        fastJsonHttpMessageConverter.setFastJsonConfig(config);
        converters.add(fastJsonHttpMessageConverter);
//        converters.add(new MappingJackson2XmlHttpMessageConverter());
//        converters.add(new MappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(converters);
        restTemplate.setErrorHandler(new RestClientErrorHandler());
        return restTemplate;
    }

//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    @Bean
    @LoadBalanced
    public AsyncRestTemplate AsyncRestTemplate() {
        return new AsyncRestTemplate();
    }


//    @Bean
//    public WebClient.Builder webClientBuilder(){
//        return WebClient.builder();
//    }


    @Bean
    ReactorResourceFactory resourceFactory() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false);
        factory.setConnectionProvider(ConnectionProvider.fixed("httpClient", 200, 50000));
        factory.setLoopResources(LoopResources.create("httpClient", 100, true));
        return factory;
    }


    @Bean
    @LoadBalanced
    WebClient.Builder webClientBuilder() {
        Function<HttpClient, HttpClient> mapper = client ->
                client.tcpConfiguration(c ->
                        c.option(CONNECT_TIMEOUT_MILLIS, 50000)
                                .option(TCP_NODELAY, true)
                                .doOnConnected(conn -> {
                                    conn.addHandlerLast(new ReadTimeoutHandler(50000));
                                    conn.addHandlerLast(new WriteTimeoutHandler(50000));
                                }));

        ClientHttpConnector connector =
                new ReactorClientHttpConnector(resourceFactory(), mapper);

        return WebClient.builder().clientConnector(connector);
    }

}
