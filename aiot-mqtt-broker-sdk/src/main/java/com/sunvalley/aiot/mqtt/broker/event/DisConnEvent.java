package com.sunvalley.aiot.mqtt.broker.event;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import org.springframework.context.ApplicationEvent;

/**
 * @author kai.li
 * @date 2020/1/23
 */
public class DisConnEvent extends ApplicationEvent {

    public DisConnEvent(MqttConnection connection) {
        super(connection);
    }
}
