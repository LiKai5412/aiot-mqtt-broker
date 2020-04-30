package com.sunvalley.aiot.mqtt.broker.utils;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;

import java.util.List;

/**
 * @author kai.li
 */
public class MqttMessageBuilder {

    public static MqttPublishMessage buildPub(MqttQoS qos, boolean isRetain, int messageId, String topic, byte[] message) {
        MqttPublishMessage mqttPublishMessage = MqttMessageBuilders.publish().qos(qos)
                .retained(isRetain).messageId(messageId).topicName(topic).payload(Unpooled.wrappedBuffer(message)).build();
        return mqttPublishMessage;
    }

    public static MqttPublishMessage buildPub(MqttMessageType mqttMessageType, MqttQoS qos, boolean isDup, boolean isRetain,
                                              int messageId, String topic, byte[] message, Integer remainingLength) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(mqttMessageType, isDup,
                qos, isRetain, remainingLength);
        MqttPublishVariableHeader from = new MqttPublishVariableHeader(topic, messageId);
        return new MqttPublishMessage(mqttFixedHeader, from, Unpooled.wrappedBuffer(message));
    }

    public static MqttPubAckMessage buildPubAck(MqttMessageType mqttMessageType, MqttQoS qos, boolean isDup,
                                                int messageId, boolean isRetain) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(mqttMessageType, isDup, qos, isRetain, 2);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        return new MqttPubAckMessage(mqttFixedHeader, from);
    }

    public static MqttConnAckMessage buildConnAck(MqttConnectReturnCode mqttConnectReturnCode, boolean sessionPresent) {
        MqttConnAckMessage mqttConnAckMessage = MqttMessageBuilders.connAck().returnCode(mqttConnectReturnCode).sessionPresent(sessionPresent).build();
        return mqttConnAckMessage;
    }

    public static MqttPubAckMessage buildPubRec(Integer messageId) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC, false,
                MqttQoS.AT_LEAST_ONCE, false, 2);
        MqttMessageIdVariableHeader from = MqttMessageIdVariableHeader.from(messageId);
        return new MqttPubAckMessage(mqttFixedHeader, from);
    }

    public static MqttSubAckMessage buildSubAck(int messageId, List<Integer> qos) {
        return buildSubAck(messageId, qos, MqttQoS.AT_MOST_ONCE);
    }

    public static MqttSubAckMessage buildSubAck(int messageId, List<Integer> qos, MqttQoS mqttQoS) {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, mqttQoS, false, 0);
        MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
        MqttSubAckPayload payload = new MqttSubAckPayload(qos);
        return new MqttSubAckMessage(mqttFixedHeader, variableHeader, payload);
    }

    public static MqttSubAckMessage buildFailureSubAck(int messageId, List<Integer> qos) {
        return buildSubAck(messageId, qos, MqttQoS.FAILURE);
    }

    public static MqttUnsubAckMessage buildUnsubAck(int messageId) {
        MqttUnsubAckMessage unsubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 2),
                MqttMessageIdVariableHeader.from(messageId), null);
        return unsubAckMessage;
    }
}
