/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttPubAckMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * PUBACK连接处理
 *
 * @author kai.li
 */
@Slf4j
public class PubAck {

    public void processPubAck(MqttConnection connection, MqttPubAckMessage msg) {
        MqttMessageIdVariableHeader variableHeader = msg.variableHeader();
        int messageId = variableHeader.messageId();
        log.debug("PUBACK - deviceId: {}, messageId: {}",
				connection.getSn(), messageId);
        connection.cancelDisposable(messageId);
    }

}
