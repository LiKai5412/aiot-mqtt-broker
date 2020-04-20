package com.sunvalley.aiot.mqtt.broker.config;

import com.sunvalley.aiot.mqtt.broker.event.listener.DisConnEventListener;
import com.sunvalley.aiot.mqtt.broker.event.listener.IdleEventListener;
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
        return new IdleEventListener();
    }

    @Bean
    @ConditionalOnMissingBean(DisConnEventListener.class)
    public DisConnEventListener disConnEventListener(){
        return new DisConnEventListener();
    }
}
