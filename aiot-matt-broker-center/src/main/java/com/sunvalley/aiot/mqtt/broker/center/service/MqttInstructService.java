package com.sunvalley.aiot.mqtt.broker.center.service;

import com.sunvalley.aiot.mqtt.broker.center.bean.mqtt.MqttInstruct;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 11:48
 * @Desc: 指令请求service
 */
public interface MqttInstructService {

    void mqttInstruct(MqttInstruct mqttInstruct);
}
