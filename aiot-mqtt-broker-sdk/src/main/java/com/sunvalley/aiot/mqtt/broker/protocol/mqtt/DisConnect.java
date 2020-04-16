/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.utils.AttributeKeys;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


/**
 * DISCONNECT连接处理
 */
@Slf4j
public class DisConnect {
    public void processDisConnect(MqttConnection connection) {
    	//清除遗嘱消息
        Optional.ofNullable(connection.getConnection().channel().attr(AttributeKeys.WILL_MESSAGE)).map(Attribute::get).ifPresent(mqttPublishMessage -> {
            connection.getConnection().channel().attr(AttributeKeys.WILL_MESSAGE).set(null);
        });
        String deviceId = connection.getDeviceId();
        log.debug("DISCONNECT - deviceId: {}, cleanSession: {}", deviceId, connection.getCleanSession());
        connection.dispose();
    }

}
