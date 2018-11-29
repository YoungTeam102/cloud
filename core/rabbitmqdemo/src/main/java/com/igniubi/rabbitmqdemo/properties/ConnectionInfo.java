package com.igniubi.rabbitmqdemo.properties;

import lombok.Data;

/**
 * rabbitmq connection info
 */
@Data
public class ConnectionInfo {
    int heartbeat = 5;   //5s   heartbeat message between client and server,   keepalive connection
    String beanId = "default"; //for connectionFactory register
    String factoryId;
    String templateId = "defaultRabbitTemplate";
    String address;
    String port;
    String username;
    String password;
    String vhost;


    public ConnectionInfo(String factoryId, int heartbeat, String templateId) {
        this.factoryId = factoryId;
        this.heartbeat = heartbeat;
        this.templateId = templateId;
    }

    public ConnectionInfo(String factoryId, int heartbeat) {
        this.factoryId = factoryId;
        this.heartbeat = heartbeat;
    }

    public ConnectionInfo(int heartbeat, String beanId, String factoryId, String templateId, String address, String port, String username, String password, String vhost) {
        this.heartbeat = heartbeat;
        this.beanId = beanId;
        this.factoryId = factoryId;
        this.templateId = templateId;
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
        this.vhost = vhost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionInfo that = (ConnectionInfo) o;
        if (heartbeat != that.heartbeat) return false;
        return factoryId.equals(that.factoryId);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + factoryId.hashCode();
        result = 31 * result + heartbeat;
        return result;
    }

}
