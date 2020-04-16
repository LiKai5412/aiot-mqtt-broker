/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.MessageManager;
import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
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

    public Subscribe(TopicManager topicManager, MessageManager messageManager) {
        this.topicManager = topicManager;
        this.messageManager = messageManager;
    }

    public void processSubscribe(MqttConnection connection, MqttSubscribeMessage msg) {
        int messageId = msg.variableHeader().messageId();
        List<Integer> grantedQoSLevels = msg.payload().topicSubscriptions().stream()
                .map(m -> m.qualityOfService().value()).collect(Collectors.toList());
        //发送subAck
        connection.sendSubAck(messageId, grantedQoSLevels).subscribe();
        msg.payload().topicSubscriptions().stream().forEach(mqttTopicSubscription -> {
            String topic = mqttTopicSubscription.topicName();
            connection.addTopic(topic);
            //保存订阅关系
            topicManager.addTopicConnection(topic, connection);
            //发布保留消息
            messageManager.getRetainMessage(topic).ifPresent(retainMessage -> {
                if (retainMessage.getQos() == 0) {
                    connection.sendPublishMessage(MqttMessageType.PUBLISH, MqttQoS.valueOf(retainMessage.getQos()), retainMessage.isDup(),
                            true, topic, null, retainMessage.getCopyByteBuf()).subscribe();
                }else {
					connection.sendPublishMessageRetry(MqttMessageType.PUBLISH, MqttQoS.valueOf(retainMessage.getQos()),
							true, topic, null, retainMessage.getCopyByteBuf()).subscribe();
				}
            });
        });
    }
}
