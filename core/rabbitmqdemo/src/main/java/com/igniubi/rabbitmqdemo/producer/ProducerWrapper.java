package com.igniubi.rabbitmqdemo.producer;

import com.alibaba.fastjson.JSON;
import com.igniubi.rabbitmqdemo.mqinterface.IProducer;
import com.igniubi.rabbitmqdemo.mqinterface.IProducerConfirmCallback;
import com.igniubi.rabbitmqdemo.properties.ProducerInfo;
import lombok.Data;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * class implements IProducer for delegate customer send message
 */

@Data
public class ProducerWrapper implements IProducer {
    public static final Logger logger = LoggerFactory.getLogger(ProducerWrapper.class);
    private static final UUID PUUID = UUID.randomUUID();
    private static final AtomicLong MID = new AtomicLong();

    // producer info
    private ProducerInfo producerInfo;

    // spring bean factory
    private DefaultListableBeanFactory beanFactory;

    //rabbitTemplate for send message
    protected RabbitTemplate rabbitTemplate;

    public ProducerWrapper() {
    }

    public ProducerWrapper(ProducerInfo producerInfo, DefaultListableBeanFactory beanFactory, RabbitTemplate rabbitTemplate) {
        this.producerInfo = producerInfo;
        this.beanFactory = beanFactory;
        this.rabbitTemplate = rabbitTemplate;
    }


    //default  message presistent
    protected MessageDeliveryMode mode = MessageDeliveryMode.PERSISTENT;

    @PostConstruct
    public void init() {
        //check message whether persistent
        if (!producerInfo.isPersistent()) {
            mode = MessageDeliveryMode.NON_PERSISTENT;
        }
        //check message whether confirm, set confirm callback if need confirm
        if (producerInfo.isConfirm()) {
            rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
                @Override
                public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                    if (!ack) {
                        //当无法投递到exchange时, 根据发送时配置的Id, 记录未confirm消息, 回调业务发送失败方法
                        logger.error("failed to send out msg id:" + correlationData.getId());
                    } else {
                        logger.info("success to send out msg id:" + correlationData.getId());
                    }

                    if (StringUtils.isNotEmpty(producerInfo.getConfirmCallback())) {
                        ((IProducerConfirmCallback) beanFactory.getBean(producerInfo.getConfirmCallback()))
                                .confirm(correlationData.getId(), ack, cause);
                    }
                }
            });
        }
    }



    /***
     * send action
     * @param topic
     * @param exchange
     * @param amqpMsg
     */
    protected void doSend(String topic, String exchange, Message amqpMsg) {
        // 如果消息头中已经指定了消息ID，则无需再次生成
        // 在需要confirmCallback时，服务层需要自己指定消息ID
        String messageUUId = generateMsgUUId();
        if (amqpMsg.getMessageProperties().getHeaders().get("messageID") != null) {
            messageUUId = String.valueOf(amqpMsg.getMessageProperties().getHeaders().get("messageID"));
        }
        amqpMsg.getMessageProperties().setCorrelationId(String.valueOf(messageUUId.getBytes()));
        amqpMsg.getMessageProperties().setDeliveryMode(mode);
        CorrelationData correlationData = new CorrelationData(messageUUId);
        getRabbitTemplate().send(exchange, topic, amqpMsg, correlationData);
    }


    /**
     * 生成全局唯一message Id
     *
     * @return
     */
    protected String generateMsgUUId() {
        return PUUID.toString() + "-" + MID.getAndIncrement();
    }


    /**
     * send message
     *
     * @param topic
     * @param object
     */
    @Override
    public void send(String topic, Object object) {
        send(topic, null, object, null);
    }

    /**
     * send message
     *
     * @param topic
     * @param object
     * @param attributes
     */
    @Override
    public void send(String topic, Object object, Map<String, Object> attributes) {
        send(topic, null, object, attributes);
    }

    /**
     * send message
     *
     * @param topic
     * @param object
     * @param attributes
     * @param delayInMinutes
     */
    @Override
    public void send(String topic, Object object, Map<String, Object> attributes, int delayInMinutes) {
        String sent_topic = "igniubi_delay." + delayInMinutes + "m." + topic;

        send(sent_topic, null, object, attributes);
    }

    /***
     * 默认topic，如跨云发送 使用 FEDERATION_TOPIC_EXCHANGE

     * @param topic
     * @param exchange
     * @param object
     */
    @Override
    public void send(String topic, String exchange, Object object) {

        send(topic, exchange, object, null);

    }

    /**
     * send message
     * @param topic
     * @param exchange
     * @param object
     * @param attributes
     */
    @Override
    public void send(String topic, String exchange, Object object, Map<String, Object> attributes) {

        //消息的属性
        MessageProperties properties = new MessageProperties();
        properties.setContentType("text");
        if (attributes != null) {
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                properties.setHeader(entry.getKey(), entry.getValue());
            }
        }

        //消息体
        byte[] body = JSON.toJSONString(object).getBytes(Charsets.toCharset("UTF-8"));

        Message amqpMsg = new Message(body, properties);

        String amqpExchange = StringUtils.isBlank(exchange) ? TOPIC_EXCHAGE : exchange;

        ((ProducerWrapper) beanFactory.getBean(producerInfo.getBeanName())).doSend(topic, amqpExchange, amqpMsg);


        logger.info("send out message id:{}, exchange:{}, topic:{}, message:{}", amqpMsg.getMessageProperties().getCorrelationId(), amqpExchange, topic, object);

    }
}
