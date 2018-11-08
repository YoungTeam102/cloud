package com.igniubi.rabbitmqdemo.mqinterface;

/**
 *
 * customer implement this interface, create bean for delegate invoke this method "handleMessage"
 *
 * if define retry in rabbitmq.yml,then delegate will catch customer exception and throw message into retry queue for
 * waiting retry invoke when "handleMessage" throw exception
 *
 */
public interface IConsumer {

    /**
     * handle message
     *
     * customer implements this interface
     *
     * @param message 消息， 是个字符串。用String.valueOf(message)转化
     */
    void handleMessage(Object message) throws Exception;
}
