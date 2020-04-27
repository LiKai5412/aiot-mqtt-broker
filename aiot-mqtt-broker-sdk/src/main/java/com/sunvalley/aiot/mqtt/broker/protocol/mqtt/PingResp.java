/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import lombok.extern.slf4j.Slf4j;

/**
 * PINGRESP连接处理
 * @author kai.li
 */
@Slf4j
public class PingResp {

	public void processPingResp(MqttConnection connection) {
		log.debug("PINGRESP - deviceId: {}", connection.getSn());
		connection.sendPingResp().subscribe();
	}

}
