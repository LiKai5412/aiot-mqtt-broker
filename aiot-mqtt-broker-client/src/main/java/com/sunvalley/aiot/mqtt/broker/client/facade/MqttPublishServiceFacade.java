package com.sunvalley.aiot.mqtt.broker.client.facade;

import com.sunvalley.aiot.mqtt.broker.client.service.MqttPublishService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 10:41
 * @Desc: mqtt-broker往设备端 发送(下行)消息
 */
@FeignClient(value = "${client.aiot.mqtt.broker.center.group:aiot-mqtt-mqtt-broker-center}", contextId = "mqttPublishServiceFacade")
public interface MqttPublishServiceFacade extends MqttPublishService {
}
