package com.sunvalley.aiot.mqtt.broker.event;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * @author kai.li
 * @date 2020/1/23
 */
@Data
public class PublishEvent extends ApplicationEvent {
    private MqttPublishMessage msg;
    private byte[] array;

    public PublishEvent(MqttConnection connection, MqttPublishMessage msg, byte[] array) {
        super(connection);
        this.msg = msg;
        this.array = array;
    }
}
