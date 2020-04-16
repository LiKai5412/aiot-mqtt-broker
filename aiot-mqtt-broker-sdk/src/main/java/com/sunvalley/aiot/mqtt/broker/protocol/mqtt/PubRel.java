/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.ClusterManager;
import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * PUBREL连接处理
 *
 * @author kai.li
 */
@Slf4j
public class PubRel {

    private TopicManager topicManager;

    private ClusterManager clusterManager;

    public PubRel(TopicManager topicManager, ClusterManager clusterManager) {
        this.topicManager = topicManager;
        this.clusterManager = clusterManager;
    }

    public void processPubRel(MqttConnection connection, MqttMessage msg) {
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) msg.variableHeader();
        int messageId = variableHeader.messageId();
        log.debug("PUBREL - deviceId: {}, messageId: {}", connection.getDeviceId(), messageId);
        connection.cancelDisposable(messageId);
        //发送qos2消息
        connection.getAndRemoveQos2Message(messageId).ifPresent(mqttPublishMessage -> {
            sendPubMessageRetry(connection, MqttQoS.valueOf(mqttPublishMessage.getQos()), mqttPublishMessage.isRetain(),
                    mqttPublishMessage.isDup(), mqttPublishMessage.getTopic(), mqttPublishMessage.getMessage());
        });
        //发送PUBCOMP
        sendPubComp(connection, messageId);
    }

    private void sendPubComp(MqttConnection connection, Integer messageId) {
        connection.sendPubAckMessage(MqttMessageType.PUBCOMP, false, false, messageId)
                .subscribe();
    }

    private void sendPubMessageRetry(MqttConnection connection, MqttQoS qos, Boolean retain, Boolean dup,
                                     String topicName, byte[] array) {
        Optional.ofNullable(topicManager.getConnectionsByTopic(topicName))
                .ifPresent(mqttConnections -> {
                    mqttConnections.stream().filter(conn -> !connection.equals(conn) && !conn.isDisposed())
                            .forEach(conn -> {
                                conn.sendPublishMessageRetry(qos, retain,
                                        topicName, array).subscribe();
                            });
                    InternalMessage internalMessage = InternalMessage.buildPubMessage(topicName,
                            qos.value(), array, retain, dup);
                    Mono.just(internalMessage).subscribe(clusterManager::sendInternalMessage);
                });
    }
}
