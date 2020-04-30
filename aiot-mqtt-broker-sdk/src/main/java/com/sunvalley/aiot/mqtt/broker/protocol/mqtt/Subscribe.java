/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.MessageManager;
import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.config.MqttTopicProperties;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * SUBSCRIBE连接处理
 *
 * @author kai.li
 */
@Slf4j
public class Subscribe {

    private TopicManager topicManager;
    private MessageManager messageManager;
    private MqttTopicProperties mqttTopicProperties;

    public Subscribe(TopicManager topicManager, MessageManager messageManager, MqttTopicProperties mqttTopicProperties) {
        this.topicManager = topicManager;
        this.messageManager = messageManager;
        this.mqttTopicProperties = mqttTopicProperties;
    }

    public void processSubscribe(MqttConnection connection, MqttSubscribeMessage msg) {
        int messageId = msg.variableHeader().messageId();
        List<Integer> grantedQoSLevels = msg.payload().topicSubscriptions().stream()
                .map(m -> m.qualityOfService().value()).collect(Collectors.toList());
        AtomicBoolean isFailure = new AtomicBoolean(false);
        msg.payload().topicSubscriptions().stream().forEach(mqttTopicSubscription -> {
            String topic = mqttTopicSubscription.topicName();
            //如果topic不以指定前缀开头或者层级不为指定数则拒绝订阅
            if (isTopicNotStartWith(topic) || mqttTopicProperties.getPathDepth() != topic.split("/").length) {
                //发送失败subAck
                connection.sendFailureSubAck(messageId, List.of(128)).subscribe();
                isFailure.set(true);
                return;
            }
            connection.addTopic(topic);
            //保存订阅关系
            topicManager.addTopicConnection(topic, connection);
            //发布保留消息
            messageManager.getRetainMessage(topic).ifPresent(retainMessage -> {
                if (retainMessage.getQos() == 0) {
                    connection.sendPublishMessage(MqttMessageType.PUBLISH, MqttQoS.valueOf(retainMessage.getQos()), retainMessage.isDup(),
                            true, topic, null, retainMessage.getCopyByteBuf()).subscribe();
                } else {
                    connection.sendPublishMessageRetry(MqttMessageType.PUBLISH, MqttQoS.valueOf(retainMessage.getQos()),
                            true, topic, null, retainMessage.getCopyByteBuf()).subscribe();
                }
            });
        });
        if (!isFailure.get()) {
            //发送subAck
            connection.sendSubAck(messageId, grantedQoSLevels).subscribe();
        }
    }

    private boolean isTopicNotStartWith(String topic) {
        if (mqttTopicProperties.getPrefixPaths() != null) {
            return mqttTopicProperties.getPrefixPaths().stream().filter(s -> topic.startsWith(s)).collect(Collectors.toList()).size() > 0 ? false : true;
        }
        return true;
    }
}
