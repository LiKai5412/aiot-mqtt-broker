package com.sunvalley.aiot.mqtt.broker.event.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.event.DisConnEvent;
import com.sunvalley.aiot.mqtt.broker.event.IdleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

/**
 * @author kai.li
 * @date 2020/2/25
 */
@Slf4j
public class DisConnEventListener implements ApplicationListener<DisConnEvent> {

    @Override
    public void onApplicationEvent(DisConnEvent disConnEvent) {
        MqttConnection connection = (MqttConnection) disConnEvent.getSource();
        log.debug("{} is going to disconnect!", connection);
    }
}
