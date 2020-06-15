/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.client.domain.po;

import lombok.Builder;
import lombok.Data;

/**
 * 集群内部消息
 *
 * @author kai.li
 */
@Data
@Builder
public class DisconnMessage {

    private String deviceId;

    private MessageType messageType;

	public static DisconnMessage buildConnMessage(String deviceId){
		return DisconnMessage.builder().deviceId(deviceId).messageType(MessageType.CONNMESSAGE).build();
	}

    public enum MessageType {
        CONNMESSAGE
    }
}
