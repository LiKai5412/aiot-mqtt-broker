package com.sunvalley.aiot.mqtt.broker.center.config;

import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.ClusterManager;
import com.sunvalley.aiot.mqtt.broker.center.listener.MqttConnEventListener;
import com.sunvalley.aiot.mqtt.broker.center.listener.MqttDisConnEventListener;
import com.sunvalley.aiot.mqtt.broker.center.listener.MqttIdleEventListener;
import com.sunvalley.aiot.mqtt.broker.center.listener.MqttPublishEventListener;
import com.sunvalley.aiot.mqtt.broker.center.listener.kfk.BrokerListener;
import com.sunvalley.aiot.mqtt.broker.event.listener.ConnEventListener;
import com.sunvalley.aiot.mqtt.broker.event.listener.DisConnEventListener;
import com.sunvalley.aiot.mqtt.broker.event.listener.IdleEventListener;
import com.sunvalley.aiot.mqtt.broker.event.listener.PublishEventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kai.li
 * @date 2020/2/25
 */
@Configuration
public class EventListenerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IdleEventListener.class)
    public IdleEventListener idleEventListener(){
        return new MqttIdleEventListener();
    }

    @Bean
    @ConditionalOnMissingBean(DisConnEventListener.class)
    public DisConnEventListener disConnEventListener(){
        return new MqttDisConnEventListener();
    }

    @Bean
    @ConditionalOnMissingBean(ConnEventListener.class)
    public ConnEventListener connEventListener(){
        return new MqttConnEventListener();
    }

    @Bean
    @ConditionalOnMissingBean(PublishEventListener.class)
    public PublishEventListener publishEventListener(){
        return new MqttPublishEventListener();
    }

    @Bean
    public BrokerListener brokerListener(TopicManager topicManager){
        return new BrokerListener(topicManager);
    }
}
