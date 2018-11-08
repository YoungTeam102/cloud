### 说明
  - 基于`spring-rabbit` 封装了对RabbitMQ消息的处理，支持`topic`, `direct`, `fanout`, `federation` 四类消息
  - 支持延迟队列
  - 支持重试队列

添加依赖： 
```xml
  <dependency>
     <groupId>com.igniubi</groupId>
    <artifactId>rabbitmqdemo</artifactId>
  </dependency>
```

### 使用说明

 -  在业务工程的配置文件`rabbitmq.yml`中添加producer、consumer信息
 -  样例：
 ```
consumer:
 -  connection: defaultConnectionFactory  连接名
    address: 47.100.235.125   ip
    port: 5672         端口
    username: admin     用户名
    password: admin     密码
    consumers:
          - class: com.igniubi.demo.rabbitmq.consumer.LogConsumer
            topic: igniubi.log
                
          - class: com.igniubi.demo.rabbitmq.consumer.userConsumer
            topic: igniubi.user
                queue: test_retry
                concurrent: 1
                prefetch: 10
                retry:
                    interval: 2
                    queue: test_retry.retry   #default

                    
 -  connection: userConnectionFactory
    address: 47.100.235.125
    port: 5672
    username: admin
    password: admin
    consumers:
          - class: com.igniubi.demo.rabbitmq.consumer.userConsumer
            topic: igniubi.user


producer:
    -   connection: defaultConnectionFactory
        address: 47.100.235.125
        port: 5672
        username: admin
        password: admin
        producers:
                  - bean: demoProducer

    -   connection: userConnectionFactory
        address: 47.100.235.125
        port: 5672
        username: admin
        password: admin
        producers:
                  - bean: userProducer
            async: true
            confirm: true
            persistent: true
 ``` 
 -  生产者参数说明：
 ```
    producer:           生产者默认起始配置项，下面按mq连接，分列表
        -   address:            mq连接地址，如果为集群，逗号分隔，例如（host1:port1,host2:port2）
            username:           mq连接用户（默认 yoho）
            password:           mq连接用户密码 （默认 yoho）
            vhost:              mq连接空间  （默认 yoho）
            producers:          producer列表
                      - bean:   bean Id 全局唯一
                        async:  是否异步发送 （默认 false）
                        confirm:  发送可靠性   （默认 true）
                        confirmcallback: 发送端确认回调参数，需指明回调bean名称
                        persistent:  消息是否持久化 （默认 false）
 ```   
 -  消费者参数说明：
 ```
    注意: 当前版本  延迟队列、 重试队列、 federation队列  仅支持一项，同时组合配置，效果不可预估
    
    consumer:          消费者默认起始配置项，下面按mq连接，分列表
         -  address:            mq连接地址，如果为集群，逗号分隔，例如（host1:port1,host2:port2）
            username:           mq连接用户
            password:           mq连接用户密码 
            vhost: /            mq连接空间  
            consumers:          consumer列表
                  - class:      消费实体类，实现IConsumer，该类的bean若不存在 服务启动失败
                    topic:      消费主题
                    queue:      消费队列 
                    ratelimit:  消费速度限制
                    delay:      是否启动延迟队列能力
                        interval:  延迟消费时间，单位为分钟  （默认 10）
                        queue:     延迟队列 （默认  delay:$intervalm:queue）
                        
                  - class: 
                    topic: 
                    queue: 
                    concurrent:     消费者并发数量 （默认 1）
                    prefetch:       单个消费者默认一次最大获取消息量  （默认 10）
                    retry:      是否开启重试队列
                        interval:   重试消费时间，单位为分钟  （默认 10）
                        queue:      重试队列 (默认 retry:$intervalm:queue)
                        
                  - class: com.yoho.core.rabbitmq.TestConsumer
                    topic: test_delay
                    ratelimit: 2
                    delay:
                        interval: 2
                        queue: test_delay.delay
                        
         -  address: 192.168.103.77:5672,192.168.103.78:5672
            consumers:
                  - class: com.yoho.core.rabbitmq.TestConsumer #bean不存在 服务启动失败
                    topic: test_common_1
                    
                  
                  - class: com.yoho.core.rabbitmq.TestConsumer #bean不存在 服务启动失败
                    topic: fed_test_topic
                    federation:  是否开启federation   
                        exchange:  federation exchange 名称         #默认 yoho.federation.topic                  
 ```
    
 

}

```
producer callback:
- bean需要实现IProducerConfirmCallback接口
- 在配置文件中配置confirmcallback为bean名称
- 如需要自定义messageID,需要在发送时传入attribute,名称为"messageID"，值为字符类型的ID

### 接收消息
 实现接口`IConsumer`，并且这个将`Bean`加入到`rabbitmq.yml`中。例如：

```java
/**
 *
 * 消息的消费者
 */
public class userConsumer implements IConsumer {
    private final Logger logger = LoggerFactory.getLogger(userConsumer.class);



    @Override
    public void handleMessage(Object message) throws Exception {
        Log log = JSONObject.parseObject(message.toString(),Log.class);
        logger.info("complete write userlog into mongodb , userlog is {}", log);
    }
   
}

```

 -  connection: userConnectionFactory
    address: 47.100.235.125
    port: 5672
    username: admin
    password: admin
    consumers:
          - class: com.igniubi.demo.rabbitmq.consumer.userConsumer
            topic: igniubi.user


如果要支持重试队列，在rabbitmq.yml中开启，同时consumer在实现IConsumer时，消息处理异常需要抛出异常