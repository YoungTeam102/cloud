package com.igniubi.rabbitmqdemo.connection;

import com.igniubi.rabbitmqdemo.properties.ConnectionInfo;
import com.igniubi.rabbitmqdemo.properties.RabbitYmlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.PriorityOrdered;

import java.util.HashSet;
import java.util.Iterator;

/**
 * <p>
 * for create connectionFactory beans
 */

public class ConnectionFactoryBuilder implements BeanFactoryPostProcessor, PriorityOrdered {
    public static final Logger logger = LoggerFactory.getLogger(ConnectionFactoryBuilder.class);

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
        logger.info("ConnectionFactoryBuilder: register rabbitmq-connection-factory define into spring");

        HashSet<ConnectionInfo> connectionInfoHashSet = RabbitYmlFactory.getConnectionInfoSet();

        if (null == connectionInfoHashSet || connectionInfoHashSet.isEmpty()) {
            logger.info("no found rabbit connection info in rabbitmq.yml...");
            return;
        }
        Iterator<ConnectionInfo> connectionInfoIterator = connectionInfoHashSet.iterator();
        while (connectionInfoIterator.hasNext()) {
            ConnectionInfo connectionInfo = connectionInfoIterator.next();
            if (!beanFactory.containsBean(connectionInfo.getFactoryId())) {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CachingConnectionFactory.class);
                builder.addPropertyValue("addresses", connectionInfo.getAddress());
                builder.addPropertyValue("username", connectionInfo.getUsername());
                builder.addPropertyValue("password", connectionInfo.getPassword());
                builder.addPropertyValue("virtualHost", connectionInfo.getVhost());
                builder.addPropertyValue("requestedHeartBeat", connectionInfo.getHeartbeat());
                builder.addPropertyValue("publisherConfirms", true);
                ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(connectionInfo.getFactoryId(), builder.getBeanDefinition());
                logger.info("register rabbitmq-connection-factory:{} into spring define map....", connectionInfo.getBeanId());

            }
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
