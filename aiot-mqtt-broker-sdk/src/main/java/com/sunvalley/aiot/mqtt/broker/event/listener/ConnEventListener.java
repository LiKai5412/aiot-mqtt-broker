package com.sunvalley.aiot.mqtt.broker.event.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.event.ConnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

/**
 * @author kai.li
 * @date 2020/2/25
 */
@Slf4j
public class ConnEventListener implements ApplicationListener<ConnEvent> {

    @Override
    public void onApplicationEvent(ConnEvent connEvent) {
        MqttConnection connection = (MqttConnection) connEvent.getSource();
        log.debug("{} is going to connect!", connection);
    }
}
