/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.utils.AttributeKeys;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;

/**
 * PUBREC连接处理
 *
 * @author kai.li
 */
@Slf4j
public class PubRec {
    public void processPubRec(MqttConnection connection, MqttMessage msg) {
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) msg.variableHeader();
        int messageId = variableHeader.messageId();
        log.debug("PUBREC - deviceId: {}, messageId: {}", connection.getAttr(AttributeKeys.DEVICE_ID), messageId);
        connection.cancelDisposable(messageId);
        //发送PUBREL
        sendPubRel(connection, messageId );
    }

    private void sendPubRel(MqttConnection connection, int messageId) {
        connection.sendPubAckMessageRetry(MqttMessageType.PUBREL, false, false,
                messageId).subscribe();
    }

}
