package com.sunvalley.aiot.mqtt.broker.api;

import com.sunvalley.aiot.mqtt.broker.common.message.TransportMessage;
import com.sunvalley.aiot.mqtt.broker.utils.AttributeKeys;
import com.sunvalley.aiot.mqtt.broker.utils.MqttMessageBuilder;
import com.google.common.collect.Lists;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.Attribute;
import lombok.Data;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

/**
 * @author kai.li
 */
@Data
public class MqttConnection implements Disposable {

    private NettyInbound inbound;

    private NettyOutbound outbound;

    private Connection connection;

    private LongAdder longAdder = new LongAdder();

    private List<String> topics = Lists.newCopyOnWriteArrayList();

    private ConcurrentHashMap<Integer, Disposable> disposableMessageHashMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer, TransportMessage> qos2Message = new ConcurrentHashMap<>();

    private TopicManager topicManager;

    private ChannelManager channelManager;

    private MessageManager messageManager;

    private Boolean cleanSession;

    public MqttConnection(Connection connection, TopicManager topicManager, ChannelManager channelManager, MessageManager messageManager) {
        this.connection = connection;
        this.inbound = connection.inbound();
        this.outbound = connection.outbound();
        this.topicManager = topicManager;
        this.channelManager = channelManager;
        this.messageManager = messageManager;
    }

    public int generateMessageId() {
        longAdder.increment();
        int value = longAdder.intValue();
        if (value == Integer.MAX_VALUE) {
            longAdder.reset();
            longAdder.increment();
            return longAdder.intValue();
        }
        return value;
    }

    @Override
    public void dispose() {
        connection.dispose();
        destroy();
    }

    @Override
    public boolean isDisposed() {
        return connection.isDisposed();
    }

    public String getSn() {
        return this.getConnection().channel().attr(AttributeKeys.DEVICE_ID).get();
    }

    public void addDisposable(Integer messageId, Disposable disposable) {
        disposableMessageHashMap.put(messageId, disposable);
    }

    public void cancelDisposable(Integer messageId) {
        Optional.ofNullable(disposableMessageHashMap.get(messageId))
                .ifPresent(dispose -> dispose.dispose());
        disposableMessageHashMap.remove(messageId);
    }

    public Mono<Void> sendPublishMessage(MqttQoS qos, boolean isRetain, String topic, byte[] message) {
        MqttPublishMessage mqttPublishMessage = MqttMessageBuilder.buildPub(qos, isRetain, generateMessageId(), topic, message);
        return getOutbound().sendObject(mqttPublishMessage).then();
    }

    public Mono<Void> sendPublishMessage(MqttMessageType mqttMessageType, MqttQoS qos, boolean isDup, boolean isRetain,
                                         String topic, Integer messageId, byte[] message) {
        if (messageId == null) {
            messageId = generateMessageId();
        }
        MqttPublishMessage mqttPublishMessage = MqttMessageBuilder.buildPub(
                mqttMessageType, qos, isDup, isRetain, messageId, topic, message, 0);
        return getOutbound().sendObject(mqttPublishMessage).then();
    }

    public Mono<Void> sendPublishMessageRetry(MqttMessageType mqttMessageType, MqttQoS qos, boolean isRetain,
                                              String topic, Integer messageId, byte[] message) {
        if (messageId == null) {
            messageId = generateMessageId();
        }
        MqttPublishMessage mqttPublishMessage = MqttMessageBuilder.buildPub(
                mqttMessageType, qos, false, isRetain, messageId, topic, message, 0);
        MqttPublishMessage retryMqttPublishMessage = MqttMessageBuilder.buildPub(
                mqttMessageType, qos, true, isRetain, messageId, topic, message, 0);
        addRetrySubscriber(messageId, retryMqttPublishMessage);
        return getOutbound().sendObject(mqttPublishMessage).then();
    }

    public Mono<Void> sendPublishMessageRetry(MqttQoS qos, boolean isRetain, String topic, byte[] message) {
        int messageId = this.generateMessageId();
        MqttPublishMessage mqttPublishMessage = MqttMessageBuilder.buildPub(qos, isRetain, messageId, topic, message);
        MqttPublishMessage retryMqttPublishMessage = MqttMessageBuilder.buildPub(
                MqttMessageType.PUBLISH, qos, true, isRetain, messageId, topic, message, 0);
        addRetrySubscriber(messageId, retryMqttPublishMessage);
        return getOutbound().sendObject(mqttPublishMessage).then();
    }

    private void addRetrySubscriber(int messageId, MqttMessage mqttMessage) {
        this.addDisposable(messageId, Mono.fromRunnable(() ->
                getOutbound().sendObject(mqttMessage).then())
                .delaySubscription(Duration.ofSeconds(10)).repeat(5).doFinally(e -> removeQos2Message(messageId))
                .subscribe());
    }

    public Mono<Void> sendPubAckMessage(MqttMessageType mqttMessageType, boolean isRetain, boolean isDup,
                                        int messageId) {
        MqttPubAckMessage pubAckMessage = MqttMessageBuilder.buildPubAck(mqttMessageType, AT_MOST_ONCE, isDup,
                messageId, isRetain);
        return getOutbound().sendObject(pubAckMessage).then();
    }

    public Mono<Void> sendPubAckMessageRetry(MqttMessageType mqttMessageType, boolean isRetain, boolean isDup,
                                             int messageId) {
        MqttPubAckMessage pubAckMessage = MqttMessageBuilder.buildPubAck(mqttMessageType, AT_MOST_ONCE, isDup,
                messageId, isRetain);
        addRetrySubscriber(messageId, pubAckMessage);
        return getOutbound().sendObject(pubAckMessage).then();
    }


    public Mono<Void> sendConnAckMessage(MqttConnectReturnCode mqttConnectReturnCode, boolean sessionPresent) {
        MqttConnAckMessage connAckMessage = MqttMessageBuilder.buildConnAck(mqttConnectReturnCode, sessionPresent);
        return getOutbound().sendObject(connAckMessage).then();
    }

    public Mono<Void> sendPingResp() {
        MqttMessage pingRespMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0), null, null);
        return getOutbound().sendObject(pingRespMessage).then();
    }

    public Mono<Void> sendPubRecMessageRetry(Integer messageId) {
        MqttPubAckMessage ackMessage = MqttMessageBuilder.buildPubRec(messageId);
        addRetrySubscriber(messageId, ackMessage);
        return getOutbound().sendObject(ackMessage).then();
    }

    public Mono<Void> sendSubAck(int messageId, List<Integer> qos) {
        MqttSubAckMessage mqttSubAckMessage = MqttMessageBuilder.buildSubAck(messageId, qos);
        return getOutbound().sendObject(mqttSubAckMessage).then();
    }

    public Mono<Void> sendFailureSubAck(int messageId, List<Integer> qos) {
        MqttSubAckMessage mqttSubAckMessage = MqttMessageBuilder.buildFailureSubAck(messageId, qos);
        return getOutbound().sendObject(mqttSubAckMessage).then();
    }

    public Mono<Void> sendUnsubAck(int messageId) {
        MqttUnsubAckMessage mqttUnsubAckMessage = MqttMessageBuilder.buildUnsubAck(messageId);
        return getOutbound().sendObject(mqttUnsubAckMessage).then();
    }

    public void saveQos2Message(Integer messageId, TransportMessage message) {
        qos2Message.put(messageId, message);
    }

    public void removeQos2Message(Integer messageId) {
        qos2Message.remove(messageId);
    }

    public Optional<TransportMessage> getAndRemoveQos2Message(Integer messageId) {
        TransportMessage message = qos2Message.get(messageId);
        qos2Message.remove(messageId);
        return Optional.ofNullable(message);
    }

    public void addTopic(String topic) {
        topics.add(topic);
    }

    public void destroy() {
        // 删除链接
        channelManager.removeConnections(this);
        // 删除topic订阅
        topics.forEach(topic -> topicManager.deleteTopicConnection(topic, this));
        // 删除device Id
        Optional.ofNullable(this.getConnection().channel().attr(AttributeKeys.DEVICE_ID))
                .map(Attribute::get)
                .ifPresent(deviceId -> {
                    channelManager.removeDeviceId(deviceId);
                    //保存订阅信息
                    if (!cleanSession) {
                        topicManager.putTopicSession(deviceId, getTopics());
                    }
                });
        disposableMessageHashMap.values().forEach(Disposable::dispose);
        disposableMessageHashMap.clear();
        topics.clear();
        qos2Message.clear();
    }
}