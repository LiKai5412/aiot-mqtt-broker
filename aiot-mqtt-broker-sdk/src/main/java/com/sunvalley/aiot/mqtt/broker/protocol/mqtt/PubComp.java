/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import lombok.extern.slf4j.Slf4j;

/**
 * PUBCOMP连接处理
 *
 * @author kai.li
 */
@Slf4j
public class PubComp {

    public void processPubComp(MqttConnection connection, MqttMessage msg) {
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) msg.variableHeader();
        int messageId = variableHeader.messageId();
        log.debug("PUBCOMP - deviceId: {}, messageId: {}", connection.getSn(), messageId);
        connection.removeQos2Message(messageId);
        connection.cancelDisposable(messageId);
    }
}
