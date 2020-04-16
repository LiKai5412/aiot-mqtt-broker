package com.sunvalley.aiot.mqtt.broker.event;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author kai.li
 * @date 2020/1/16
 */
@Slf4j
public class IdleEvent extends ApplicationEvent {

    public IdleEvent(MqttConnection connection) {
        super(connection);
    }
}
