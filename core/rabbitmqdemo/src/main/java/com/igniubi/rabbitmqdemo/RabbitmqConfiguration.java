package com.igniubi.rabbitmqdemo;

import com.igniubi.rabbitmqdemo.connection.ConnectionFactoryBuilder;
import com.igniubi.rabbitmqdemo.consumer.ConsumerFactory;
import com.igniubi.rabbitmqdemo.producer.ProducerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(basePackages = "com.igniubi.rabbitmqdemo")
public class RabbitmqConfiguration {


    @Bean(name = "rabbit-simpleMessageConverter")
    public SimpleMessageConverter getSimpleMessageConverter() {
        return new SimpleMessageConverter();
    }

    @Bean(name = "defaultConnectionFactory")
    @Primary
    public ConnectionFactory hospSyncConnectionFactory(
            @Value("${spring.rabbitmq.host}") String host,
            @Value("${spring.rabbitmq.port}") int port,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password,
            @Value("${spring.rabbitmq.virtual-host}") String virtualHost) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);

        return connectionFactory;
    }

    @Bean(name = "defaultRabbitTemplate")
    @Primary
    public RabbitTemplate firstRabbitTemplate(
            @Qualifier("defaultConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(getSimpleMessageConverter());
        return template;
    }

    @Bean(name = "core-rabbit-connection-factory")
    public ConnectionFactoryBuilder ConnectionFactoryBuilder() {
        return new ConnectionFactoryBuilder();
    }

    @Bean(name = "core-rabbit-consumer-factory")
    public ProducerFactory getProducerFactory() {
        return new ProducerFactory();
    }

    @Bean(name = "core-rabbit-producer-factory")
    public ConsumerFactory getConsumerFactory() {
        return new ConsumerFactory();
    }
}
