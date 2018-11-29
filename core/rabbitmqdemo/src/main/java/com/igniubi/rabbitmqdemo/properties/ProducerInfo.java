package com.igniubi.rabbitmqdemo.properties;

import lombok.Data;

/**
 * producer info
 */
@Data
public class ProducerInfo {
    ConnectionInfo connection; // rabbitmq connection info
    String beanName; // producer bean name for bean factory register
    boolean isAsync = false; // send async
    boolean isConfirm = true; // send confirm
    String confirmCallback;   // send confirm callback
    boolean isPersistent = true; // message persistent

    public ProducerInfo() {
    }

    public ProducerInfo(String beanName, ConnectionInfo connectionInfo) {
        this.beanName = beanName;
        this.connection = connectionInfo;
    }
}
