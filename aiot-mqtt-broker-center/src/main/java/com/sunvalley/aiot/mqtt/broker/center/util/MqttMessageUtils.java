package com.sunvalley.aiot.mqtt.broker.center.util;


import com.google.common.base.Charsets;
import com.sunvalley.aiot.mqtt.broker.api.ChannelManager;
import com.sunvalley.aiot.mqtt.broker.api.MemoryChannelManager;
import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.client.domain.web.CommandBo;
import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import com.sunvalley.aiot.mqtt.broker.config.MqttTcpServerProperties;
import com.sunvalley.aiot.mqtt.broker.utils.MqttMessageBuilder;

import io.netty.handler.codec.mqtt.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static io.netty.handler.codec.mqtt.MqttQoS.AT_MOST_ONCE;

/**
 * @author Kai.Li
 * @date 2020/3/16
 */
@Component
public class MqttMessageUtils {

    private static ChannelManager channelManager;

    private final static String IOT_GET = "iot/get/";

    private static KafkaTemplate kafkaTemplate;

    private static MqttTcpServerProperties mqttTcpServerProperties;

    private static String responseInternalTopic;

    public MqttMessageUtils(KafkaTemplate kafkaTemplate, MqttTcpServerProperties mqttTcpServerProperties,
                            @Value("${mqtt.kafka.response-internal-topic}") String responseInternalTopic) {
        MqttMessageUtils.kafkaTemplate = kafkaTemplate;
        MqttMessageUtils.mqttTcpServerProperties = mqttTcpServerProperties;
        MqttMessageUtils.responseInternalTopic = responseInternalTopic;
    }


    @Value("#{channelManager}")
    public void setMemoryChannelManager(ChannelManager channelManager) {
        MqttMessageUtils.channelManager = channelManager;
    }

    public static void sendPublishMessage(CommandBo commandBo) {
        sendPublishMessage(commandBo.getSn(), commandBo.getProductKey(), commandBo.getCommand().toString());
    }

    public static void sendPublishMessage(String sn, String productKey, String message) {
        sendPublishMessage(sn, MqttQoS.AT_MOST_ONCE, false, IOT_GET.concat(productKey).concat("/").concat(sn), message);
    }

    @SneakyThrows
    public static void sendPublishMessage(String sn, MqttQoS qos, boolean isRetain, String topic, String message) {
        MqttConnection mqttConnection = channelManager.getConnectionByDeviceId(sn);
        if (mqttConnection != null) {
            MqttPublishMessage mqttPublishMessage = MqttMessageBuilder.buildPub(qos, isRetain, mqttConnection.generateMessageId(),
                    topic, message.getBytes(Charsets.UTF_8));
            mqttConnection.getOutbound().sendObject(mqttPublishMessage).then().subscribe();
        } else {
            InternalMessage internalMessage = InternalMessage.builder().messageType(InternalMessage.MessageType.PUBMESSAGE).deviceId(sn)
                    .messageBytes(message.getBytes(StandardCharsets.UTF_8)).mqttQoS(AT_MOST_ONCE.value()).dup(false)
                    .retain(false).topic(topic).nodeId(String.valueOf(mqttTcpServerProperties.getNodeId())).build();
            kafkaTemplate.send(responseInternalTopic, internalMessage);
        }
    }

    public static void sendPublishMessageRetry(String sn, String topic, String message) {
        sendPublishMessageRetry(sn, MqttQoS.AT_MOST_ONCE, false, topic, message);
    }

    public static void sendPublishMessageRetry(String sn, MqttQoS qos, boolean isRetain,
                                               String topic, String message) {
        MqttConnection mqttConnection = channelManager.getConnectionByDeviceId(sn);
        if (mqttConnection == null) {
            return;
        }
        int messageId = mqttConnection.generateMessageId();
        byte[] bytes = message.getBytes(Charsets.UTF_8);
        MqttPublishMessage mqttPublishMessage = MqttMessageBuilder.buildPub(
                MqttMessageType.PUBLISH, qos, false, isRetain, messageId, topic, bytes, 0);
        MqttPublishMessage retryMqttPublishMessage = MqttMessageBuilder.buildPub(
                MqttMessageType.PUBLISH, qos, true, isRetain, messageId, topic, bytes, 0);
        addRetrySubscriber(mqttConnection, messageId, retryMqttPublishMessage);
        mqttConnection.getOutbound().sendObject(mqttPublishMessage).then().subscribe();
    }

    public static void sendPubAckMessage(String sn, boolean isRetain, boolean isDup,
                                         int messageId) {
        MqttConnection mqttConnection = channelManager.getConnectionByDeviceId(sn);
        if (mqttConnection == null) {
            return;
        }
        MqttPubAckMessage pubAckMessage = MqttMessageBuilder.buildPubAck(MqttMessageType.PUBACK, AT_MOST_ONCE, isDup,
                messageId, isRetain);
        mqttConnection.getOutbound().sendObject(pubAckMessage).then().subscribe();
    }

    public static void sendPubAckMessageRetry(String sn, boolean isRetain, boolean isDup,
                                              int messageId) {
        MqttConnection mqttConnection = channelManager.getConnectionByDeviceId(sn);
        if (mqttConnection == null) {
            return;
        }
        MqttPubAckMessage pubAckMessage = MqttMessageBuilder.buildPubAck(MqttMessageType.PUBACK, AT_MOST_ONCE, isDup,
                messageId, isRetain);
        addRetrySubscriber(mqttConnection, messageId, pubAckMessage);
        mqttConnection.getOutbound().sendObject(pubAckMessage).then().subscribe();
    }

    private static void addRetrySubscriber(MqttConnection mqttConnection, int messageId, MqttMessage mqttMessage) {
        mqttConnection.addDisposable(messageId, Mono.fromRunnable(() ->
                mqttConnection.getOutbound().sendObject(mqttMessage).then())
                .delaySubscription(Duration.ofSeconds(10)).repeat(5).doFinally(e -> removeQos2Message(mqttConnection, messageId))
                .subscribe());
    }

    private static void removeQos2Message(MqttConnection mqttConnection, Integer messageId) {
        mqttConnection.getQos2Message().remove(messageId);
    }
}
