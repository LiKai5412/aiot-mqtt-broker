/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.common.message;

import lombok.Builder;
import lombok.Data;

/**
 * 集群内部消息
 *
 * @author kai.li
 */
@Data
@Builder
public class InternalMessage {

    private String topic;

    private int mqttQoS;

    private byte[] messageBytes;

    private boolean retain;

    private boolean dup;

    private String nodeId;

    private String deviceId;

    private String productKey;

    private MessageType messageType;

    public static InternalMessage buildPubMessage(String topic, int mqttQoS, byte[] messageBytes, boolean retain, boolean dup) {
        return InternalMessage.builder().topic(topic).mqttQoS(mqttQoS)
				.messageBytes(messageBytes).retain(retain).dup(dup).messageType(MessageType.PUBMESSAGE).build();
    }

	public static InternalMessage buildConnMessage(String deviceId){
		return InternalMessage.builder().deviceId(deviceId).messageType(MessageType.CONNMESSAGE).build();
	}

    public enum MessageType {
        /**
         * 转发消息类型
         */
        PUBMESSAGE,
        /**
         * 连接消息类型
         */
        CONNMESSAGE,
        /**
         * 下发消息类型
         */
        RESPMESSAE
    }
}
