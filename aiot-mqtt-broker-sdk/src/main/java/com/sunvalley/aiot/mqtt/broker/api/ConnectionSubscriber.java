package com.sunvalley.aiot.mqtt.broker.api;

import com.sunvalley.aiot.mqtt.broker.config.MqttTcpServerProperties;
import com.sunvalley.aiot.mqtt.broker.event.IdleEvent;
import com.sunvalley.aiot.mqtt.broker.event.pulisher.MqttEventPublisher;
import com.sunvalley.aiot.mqtt.broker.protocol.ProtocolProcessor;
import com.sunvalley.aiot.mqtt.broker.utils.AttributeKeys;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.Attribute;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyInbound;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author kai.li
 */
@Slf4j
@Data
public class ConnectionSubscriber implements Consumer<MqttConnection> {
    private TopicManager topicManager;

    private ProtocolProcessor protocolProcessor;

    private MqttTcpServerProperties mqttTcpServerProperties;

    private MqttEventPublisher mqttEventPublisher;

    public ConnectionSubscriber(TopicManager topicManager, ProtocolProcessor protocolProcessor, MqttTcpServerProperties mqttTcpServerProperties, MqttEventPublisher mqttEventPublisher) {
        this.topicManager = topicManager;
        this.protocolProcessor = protocolProcessor;
        this.mqttTcpServerProperties = mqttTcpServerProperties;
        this.mqttEventPublisher = mqttEventPublisher;
    }

    @Override
    public void accept(MqttConnection connection) {
        NettyInbound inbound = connection.getInbound();
        Connection c = connection.getConnection();
        // 定时关闭
        Disposable disposable = Mono.fromRunnable(c::dispose)
                .delaySubscription(Duration.ofSeconds(10))
                .subscribe();
        // 设置close
        c.channel().attr(AttributeKeys.CLOSE_CONNECTION).set(disposable);
        c.onReadIdle(mqttTcpServerProperties.getHeartInSecond() * 1000, () ->
                mqttEventPublisher.publishEvent(new IdleEvent(connection)));
        c.onDispose(() -> {
            //处理遗嘱信息
            Optional.ofNullable(c.channel().attr(AttributeKeys.WILL_MESSAGE)).map(Attribute::get)
                    .ifPresent(willMessage -> {
                        List<MqttConnection> connectionsByTopic = topicManager.getConnectionsByTopic(willMessage.variableHeader().topicName());
                        connectionsByTopic.forEach(mqttConnection -> {
                            MqttQoS qos = willMessage.fixedHeader().qosLevel();
                            switch (qos) {
                                case AT_LEAST_ONCE:
                                case EXACTLY_ONCE:
                                    mqttConnection.sendPublishMessageRetry(qos, willMessage.fixedHeader().isRetain(), willMessage.variableHeader().topicName(), willMessage.payload().array()).subscribe();
                                    break;
                                default:
                                    mqttConnection.sendPublishMessage(qos, willMessage.fixedHeader().isRetain(), willMessage.variableHeader().topicName(), willMessage.payload().array()).subscribe();
                                    break;
                            }
                        });
                    });
            connection.destroy();
        });
        inbound.receiveObject().cast(MqttMessage.class)
                .subscribe(message -> protocolProcessor.process(connection, message));
    }
}
