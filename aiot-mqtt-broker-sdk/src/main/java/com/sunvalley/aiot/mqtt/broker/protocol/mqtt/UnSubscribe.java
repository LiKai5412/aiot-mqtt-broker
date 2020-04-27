/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * UNSUBSCRIBE连接处理
 * @author kai.li
 */
@Slf4j
public class UnSubscribe {
	private TopicManager topicManager;

	public UnSubscribe(TopicManager topicManager){
		this.topicManager = topicManager;
	}

	public void processUnSubscribe(MqttConnection connection, MqttUnsubscribeMessage msg) {
		List<String> topics = msg.payload().topics();
		String deviceId = connection.getSn();
		topics.forEach(topic -> {
			topicManager.deleteTopicConnection(topic, connection);
			log.debug("UNSUBSCRIBE - deviceId: {}, topic: {}", deviceId, topic);
		});
		//返回取消订阅应答
		connection.sendUnsubAck(msg.variableHeader().messageId()).subscribe();
	}

}
