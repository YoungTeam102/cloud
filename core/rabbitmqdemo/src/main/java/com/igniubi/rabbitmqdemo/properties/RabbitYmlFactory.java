package com.igniubi.rabbitmqdemo.properties;

import lombok.Getter;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.*;

/**
 * <p>
 * for read rabbitmq.yml
 */
public class RabbitYmlFactory {
    private static final Logger logger = LoggerFactory.getLogger(RabbitYmlFactory.class);

    // consumer info list
    @Getter
    private static List<ConsumerInfo> consumerInfoList = new ArrayList<>();

    //producer info list
    @Getter
    private static List<ProducerInfo> producerInfoList = new ArrayList<>();

    //connection info list
    @Getter
    private static HashSet<ConnectionInfo> connectionInfoSet = new HashSet<>();

    // web context
    private static String webContext;

    //class load read yaml
    static {
        readRabbitYml();
    }


    public static void readRabbitYml() throws BeansException {
        YamlMapFactoryBean yaml = new YamlMapFactoryBean();
        ClassPathResource oneResource = new ClassPathResource("rabbitmq.yml");
        if (!oneResource.exists()) {
            logger.error("not found rabbitmq.yml in classpath...");
            return;
        }
        yaml.setResources(oneResource);

        Map<String, Object> rabbitmqYml = yaml.getObject();

        try {
            Configuration configuration = new PropertiesConfiguration(RabbitYmlFactory.class.getClassLoader().getResource("application.properties"));
            if (null != configuration) {
                webContext = Optional.ofNullable(configuration.getString("server.servlet.context-path")).orElse("default");
            }

        } catch (ConfigurationException e) {
            logger.error("not found config.properties in classpath...");
            webContext = "default";
        }
        // read consumers
        readConsumers(rabbitmqYml);

        //read producers
        readProducers(rabbitmqYml);
    }

    /**
     * read  consumers defined in rabbitmq.yml
     *
     * @param rabbitmqYml
     * @throws Exception
     */
    private static void readConsumers(Map<String, Object> rabbitmqYml) throws BeansException {
        logger.info("RabbitYmlFactory:read consumers");
        List<Object> connections = (List<Object>) rabbitmqYml.get("consumer");

        if (null == connections) return;
        for (Object oneConn : connections) {
            Map<String, Object> connMap = (Map<String, Object>) oneConn;

            List<Object> consumerList = (List<Object>) connMap.get("consumers");
            //if no consumers are in this connection ， continue
            if (null == consumerList) continue;

            // construct connection info
            String connection = (String) connMap.get("connection");
            Integer heartbeat = Optional.ofNullable((Integer) connMap.get("hearbeat")).orElse(5);
            String template = Optional.ofNullable((String) connMap.get("connection")).orElse("default");
            String address = Optional.ofNullable((String) connMap.get("address")).orElse("47.100.235.125");
            String port = Optional.ofNullable((Integer) connMap.get("port")).orElse(5672).toString();
            String username = Optional.ofNullable((String) connMap.get("username")).orElse("admin");
            String password = Optional.ofNullable((String) connMap.get("password")).orElse("admin");
            String vhost = Optional.ofNullable((String) connMap.get("vhost")).orElse("test");
            template = template + "RabbitTemplate";
            ConnectionInfo consumerConn = new ConnectionInfo(heartbeat, connection,connection,template,address,port,username,password,vhost);
//            String connectionBean = "rabbit-connection-" + consumerConn.hashCode();
            consumerConn.setBeanId(connection);

            connectionInfoSet.add(consumerConn);

            //construct consumer info
            for (Object oneConsumer : consumerList) {
                ConsumerInfo info = new ConsumerInfo();
                Map<String, Object> consumerMap = (Map<String, Object>) oneConsumer;
                info.setConnection(consumerConn);
                info.setConsumeClass((String) consumerMap.get("class"));

                info.setTopic((String) consumerMap.get("topic"));
                //default yoho:webcontext:topic
                String queue = Optional.ofNullable((String) consumerMap.get("queue")).orElse(info.getTopic());
                info.setQueue("igniubi:" + webContext + ":" + queue);

                info.setConcurrent(Optional.ofNullable((Integer) consumerMap.get("concurrent")).orElse(1));
                info.setPrefetch(Optional.ofNullable((Integer) consumerMap.get("prefetch")).orElse(10));

                //set federation config
                if (consumerMap.containsKey("federation")) {
                    info.setFederation(true);
                    Map<String, Object> fed = (Map<String, Object>) consumerMap.get("federation");
                    if (null != fed) {
                        info.setFedExchange(Optional.ofNullable((String) fed.get("exchange")).orElse("federation.topic"));
                    } else {
                        info.setFedExchange("federation.topic");
                    }
                }

                //if contains retry
                if (consumerMap.containsKey("retry")) {
                    info.setRetry(true);
                    //default yoho_retry:webcontext:topic
                    info.setQueue("igniubi_retry:" + webContext + ":" + queue);
                    Map<String, Object> retry = (Map<String, Object>) consumerMap.get("retry");
                    if (retry != null) {
                        info.setRetryInterval(Optional.of((Integer) retry.get("interval")).orElse(10));
                        //default queueName:retry:10m
                        info.setRetryQueue(Optional.ofNullable((String) retry.get("queue")).orElse("retry:" + info.getRetryInterval() + "m" + ".queue"));
                    } else {
                        info.setRetryInterval(10);
                        info.setRetryQueue("retry:" + info.getRetryInterval() + "m" + ".queue");
                    }
                }
                //if contains delay
                if (consumerMap.containsKey("delay")) {
                    info.setDelay(true);
                    info.setQueue("igniubi_delay:" + webContext + ":" + queue);
                    Map<String, Object> delay = (Map<String, Object>) consumerMap.get("delay");
                    if (null != delay) {
                        //default delay:10m.queue
                        info.setDelayInterval(Optional.ofNullable((Integer) delay.get("interval")).orElse(10));
                        info.setDelayQueue(Optional.ofNullable((String) delay.get("queue")).orElse("delay:" + info.getDelayInterval() + "m" + ".queue"));
                    } else {
                        info.setDelayInterval(10);
                        info.setDelayQueue("delay:" + info.getDelayInterval() + "m" + ".queue");
                    }
                }
                //if  contains rateLimit
                Integer rate = Optional.ofNullable((Integer) consumerMap.get("ratelimit")).orElse(0);
                if (0 < rate) {
                    info.setRateLimit(true);
                    info.setRateLimiter(rate);
                }
                String beanId = "consumer-" + UUID.randomUUID().toString();
                info.setBeanName(beanId);
                consumerInfoList.add(info);
            }
        }
        logger.info("RabbitYmlFactory: consumers info {}", consumerInfoList);
    }

    /**
     * read producers defined in rabbitmq.yml
     *
     * @param rabbitmqYml
     */
    private static void readProducers(Map<String, Object> rabbitmqYml) {
        logger.info("RabbitYmlFactory:read producers");
        List<Object> allProducers = (List<Object>) rabbitmqYml.get("producer");
        if (null == allProducers) {
            logger.info("not found producers config in rabbitmq.yml");
            return;
        }
        for (Object oneConn : allProducers) {
            Map<String, Object> connMap = (Map<String, Object>) oneConn;
            List<Object> producers = (List<Object>) connMap.get("producers");
            if (null == producers) continue;

            // construct connection connection
            String connection = (String) connMap.get("connection");
            Integer heartbeat = Optional.ofNullable((Integer) connMap.get("hearbeat")).orElse(5);
            String template = Optional.ofNullable((String) connMap.get("connection")).orElse("default");
            String address = Optional.ofNullable((String) connMap.get("address")).orElse("47.100.235.125");
            String port = Optional.ofNullable((Integer) connMap.get("port")).orElse(5672).toString();
            String username = Optional.ofNullable((String) connMap.get("username")).orElse("admin");
            String password = Optional.ofNullable((String) connMap.get("password")).orElse("admin");
            String vhost = Optional.ofNullable((String) connMap.get("vhost")).orElse("test");
            template = template + "RabbitTemplate";
            ConnectionInfo producerConn = new ConnectionInfo(heartbeat, connection,connection,template,address,port,username,password,vhost);

//            String connectionBean = "rabbit-connection-" + producerConn.hashCode();
            producerConn.setBeanId(connection);
            connectionInfoSet.add(producerConn);

            //construct producer info
            for (Object oneProducer : producers) {
                Map<String, Object> producerMap = (Map<String, Object>) oneProducer;
                ProducerInfo info = new ProducerInfo();
                info.setConnection(producerConn);
                info.setAsync(Optional.ofNullable((Boolean) producerMap.get("async")).orElse(false));
                info.setConfirm(Optional.ofNullable((Boolean) producerMap.get("confirm")).orElse(true));
                info.setConfirmCallback(Optional.ofNullable((String)producerMap.get("confirmcallback")).orElse(""));
                info.setPersistent(Optional.ofNullable((Boolean) producerMap.get("persistent")).orElse(false));
                String beanId = Optional.ofNullable((String) producerMap.get("bean")).orElse("producer-" + UUID.randomUUID().toString());
                info.setBeanName(beanId);
                producerInfoList.add(info);
            }
        }

        logger.info("RabbitYmlFactory: producers info {}", producerInfoList);
    }
}
