/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.MessageManager;
import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.ClusterManager;
import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import com.sunvalley.aiot.mqtt.broker.common.message.TransportMessage;
import com.sunvalley.aiot.mqtt.broker.event.PublishEvent;
import com.sunvalley.aiot.mqtt.broker.event.pulisher.MqttEventPublisher;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * PUBLISH连接处理
 *
 * @author kai.li
 */
@Slf4j
public class Publish {

    private MessageManager messageManager;

    private TopicManager topicManager;

    private ClusterManager clusterManager;

    private MqttEventPublisher mqttEventPublisher;

    public Publish(MessageManager messageManager, TopicManager topicManager, ClusterManager clusterManager, MqttEventPublisher mqttEventPublisher) {
        this.messageManager = messageManager;
        this.topicManager = topicManager;
        this.clusterManager = clusterManager;
        this.mqttEventPublisher = mqttEventPublisher;
    }

    public void processPublish(MqttConnection connection, MqttPublishMessage msg) {
        MqttFixedHeader header = msg.fixedHeader();
        byte[] array = copyByteBuf(msg.payload());
        // retain=1, 保留消息
        if (msg.fixedHeader().isRetain()) {
            if (array.length == 0) {
                messageManager.removeRetainMessage(msg.variableHeader().topicName());
                return;
            } else {
                messageManager.saveRetainMessage(msg.variableHeader().topicName(), header.isDup(), header.qosLevel().value(), array);
            }
        }
        MqttPublishVariableHeader variableHeader = msg.variableHeader();
        switch (header.qosLevel()) {
            case AT_MOST_ONCE:
                sendPubMessage(connection, header, variableHeader, msg, array);
                break;
            case AT_LEAST_ONCE:
                sendPubMessageRetry(connection, header, variableHeader, msg, array);
                sendPubAckMessage(connection, variableHeader.packetId());
                break;
            case EXACTLY_ONCE:
                TransportMessage transportMessage = TransportMessage.builder().isRetain(header.isRetain())
                        .isDup(false)
                        .topic(variableHeader.topicName())
                        .message(array)
                        .qos(header.qosLevel().value())
                        .build();
                connection.saveQos2Message(variableHeader.packetId(), transportMessage);
                sendPubRecMessageRetry(connection, variableHeader);
                break;
            case FAILURE:
                log.error(" publish FAILURE {} {} ", header, variableHeader);
                break;
            default:
                break;
        }
        mqttEventPublisher.publishEvent(new PublishEvent(connection, ((long) (array.length))));
    }

    private void sendPubAckMessage(MqttConnection connection, int msgId) {
        connection.sendPubAckMessage(MqttMessageType.PUBACK,false, false, msgId).subscribe();
    }

    private void sendPubMessage(MqttConnection connection, MqttFixedHeader header,
                                MqttPublishVariableHeader variableHeader, MqttPublishMessage msg, byte[] array) {
        Optional.ofNullable(topicManager.getConnectionsByTopic(variableHeader.topicName()))
                .ifPresent(mqttConnections -> {
                    mqttConnections.stream().filter(conn -> !connection.equals(conn) && !conn.isDisposed())
                            .forEach(conn -> {
                                conn.sendPublishMessage(header.qosLevel(), header.isRetain(),
                                        variableHeader.topicName(), array).subscribe();
                            });
                    sendInternalMessage(msg, array);
                });
    }

    private void sendPubMessageRetry(MqttConnection connection, MqttFixedHeader header,
                                     MqttPublishVariableHeader variableHeader, MqttPublishMessage msg, byte[] array) {
        Optional.ofNullable(topicManager.getConnectionsByTopic(variableHeader.topicName()))
                .ifPresent(mqttConnections -> {
                    mqttConnections.stream().filter(conn -> !connection.equals(conn) && !conn.isDisposed())
                            .forEach(conn -> {
                                conn.sendPublishMessageRetry(header.qosLevel(), header.isRetain(),
                                        variableHeader.topicName(), array).subscribe();
                            });
                    sendInternalMessage(msg, array);
                });
    }

    //通知集群其它节点发布消息
    private void sendInternalMessage(MqttPublishMessage msg, byte[] array) {
        Mono.just(createInternalMessage(msg, array)).subscribe(clusterManager::sendInternalMessage);
    }

    private void sendPubRecMessageRetry(MqttConnection connection, MqttPublishVariableHeader variableHeader) {
        connection.sendPubRecMessageRetry(variableHeader.packetId()).subscribe();
    }

    private byte[] copyByteBuf(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    private InternalMessage createInternalMessage(MqttPublishMessage msg, byte[] array) {
        MqttFixedHeader header = msg.fixedHeader();
        MqttPublishVariableHeader variableHeader = msg.variableHeader();
        return InternalMessage.buildPubMessage(variableHeader.topicName(), header.qosLevel().value(),
                array, header.isRetain(), header.isDup());
    }

}
