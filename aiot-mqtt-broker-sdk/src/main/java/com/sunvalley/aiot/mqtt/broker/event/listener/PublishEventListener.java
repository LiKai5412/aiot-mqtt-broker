package com.sunvalley.aiot.mqtt.broker.event.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.event.ConnEvent;
import com.sunvalley.aiot.mqtt.broker.event.PublishEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

/**
 * @author kai.li
 * @date 2020/2/25
 */
@Slf4j
public class PublishEventListener implements ApplicationListener<PublishEvent> {

    @Override
    public void onApplicationEvent(PublishEvent publishEvent) {
        MqttConnection connection = (MqttConnection) publishEvent.getSource();
        log.debug("{} is going to publish!", connection);
    }
}
