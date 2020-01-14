package com.sunvalley.aiot.mqtt.broker.center.service;

import com.sunvalley.aiot.mqtt.broker.center.bean.mqtt.MqttBeat;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 11:48
 * @Desc: 心跳请求service
 */
public interface MqttBeatService {

    void mqttBeat(MqttBeat mqttBeat);
}
