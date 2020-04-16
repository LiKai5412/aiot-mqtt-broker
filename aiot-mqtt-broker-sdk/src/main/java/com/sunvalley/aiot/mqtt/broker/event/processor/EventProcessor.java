package com.sunvalley.aiot.mqtt.broker.event.processor;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kai.li
 * @date 2020/1/16
 */
@Data
@Slf4j
public class EventProcessor {

    public void processIdleEvent(MqttConnection connection){
        log.debug("{} is going to idle!", connection);
        connection.dispose();
    }


}
