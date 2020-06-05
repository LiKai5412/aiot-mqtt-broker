/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.utils.AttributeKeys;
import lombok.extern.slf4j.Slf4j;

/**
 * PINGRESP连接处理
 * @author kai.li
 */
@Slf4j
public class PingResp {

	public void processPingResp(MqttConnection connection) {
		log.debug("PINGRESP - deviceId: {}", connection.getAttr(AttributeKeys.DEVICE_ID));
		connection.sendPingResp().subscribe();
	}

}
