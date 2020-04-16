package com.sunvalley.aiot.mqtt.broker.event.pulisher;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.event.processor.EventProcessor;
import com.sunvalley.aiot.mqtt.broker.event.IdleEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * @author kai.li
 * @date 2020/2/25
 */
public class MqttEventPublisher implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;
    private static EventProcessor eventProcessor = new EventProcessor();

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishEvent(ApplicationEvent applicationEvent) {
        if (applicationEventPublisher == null) {
            if (applicationEvent instanceof IdleEvent) {
                eventProcessor.processIdleEvent((MqttConnection) applicationEvent.getSource());
            }
        } else {
            applicationEventPublisher.publishEvent(applicationEvent);
        }
    }
}
