package com.sunvalley.aiot.mqtt.broker.event.pulisher;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @author kai.li
 * @date 2020/2/25
 */
public class MqttEventPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishEvent(ApplicationEvent applicationEvent) {
        applicationEventPublisher.publishEvent(applicationEvent);
    }
}
