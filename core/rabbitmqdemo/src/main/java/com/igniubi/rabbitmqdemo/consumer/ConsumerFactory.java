package com.igniubi.rabbitmqdemo.consumer;


import com.google.common.util.concurrent.RateLimiter;
import com.igniubi.rabbitmqdemo.properties.ConnectionInfo;
import com.igniubi.rabbitmqdemo.properties.ConsumerInfo;
import com.igniubi.rabbitmqdemo.properties.RabbitYmlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.PriorityOrdered;

import java.util.List;

/***
 * consumer factory
 *
 * construct consumers delegate
 */
public class ConsumerFactory implements BeanFactoryPostProcessor, PriorityOrdered {

    private final static Logger logger = LoggerFactory.getLogger(ConsumerFactory.class);


    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        logger.info("ConsumerFactory: register rabbitmq-consumer-wrapper define into spring");
        List<ConsumerInfo> consumerInfoList = RabbitYmlFactory.getConsumerInfoList();

        //construct consumer
        for (ConsumerInfo oneConsumer : consumerInfoList) {
            //construct producer
            ConnectionInfo rabbitConnection = oneConsumer.getConnection();

            // register rabbitAdmin bean
            String rabbitAdminBeanId = "rabbitAdmin-" + rabbitConnection.getFactoryId();
            if (!beanFactory.containsBean(rabbitAdminBeanId)) {
                BeanDefinitionBuilder rabbitAdminBuilder = BeanDefinitionBuilder.genericBeanDefinition(RabbitAdmin.class);
                rabbitAdminBuilder.addConstructorArgReference(rabbitConnection.getFactoryId());
                ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(rabbitAdminBeanId, rabbitAdminBuilder.getBeanDefinition());
            }

            //construct consumer bean define builder for register spring bean
            BeanDefinitionBuilder consumerWrapperBuilder = BeanDefinitionBuilder.genericBeanDefinition(ConsumerWrapper.class);

            consumerWrapperBuilder.addPropertyValue("beanFactory", ((DefaultListableBeanFactory) beanFactory));
            consumerWrapperBuilder.addPropertyValue("consumerInfo", oneConsumer);

            consumerWrapperBuilder.addPropertyReference("converter", "rabbit-simpleMessageConverter");
            consumerWrapperBuilder.addPropertyReference("connectionFactory", rabbitConnection.getFactoryId());
            consumerWrapperBuilder.addPropertyReference("rabbitAdmin", rabbitAdminBeanId);

            //check consumer whether need limit consume rate
            if (oneConsumer.isRateLimit()) {
                consumerWrapperBuilder.addPropertyValue("rateLimiter", RateLimiter.create(oneConsumer.getRateLimiter()));
            }

            //register bean
            ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(oneConsumer.getBeanName(), consumerWrapperBuilder.getBeanDefinition());
            logger.info("register rabbitmq-consumer-wrapper:{} into spring define map....", oneConsumer.getBeanName());
        }
    }

    @Override
    public int getOrder() {
        //make it as early as possible
        return 3;
    }
}
