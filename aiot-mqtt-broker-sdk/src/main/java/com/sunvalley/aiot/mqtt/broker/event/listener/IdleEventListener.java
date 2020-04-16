package com.sunvalley.aiot.mqtt.broker.event.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.event.IdleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

/**
 * @author kai.li
 * @date 2020/2/25
 */
@Slf4j
public class IdleEventListener implements ApplicationListener<IdleEvent> {

    @Override
    public void onApplicationEvent(IdleEvent idleEvent) {
        MqttConnection connection = (MqttConnection) idleEvent.getSource();
        log.debug("{} is going to idle!", connection);
        connection.dispose();
    }
}
