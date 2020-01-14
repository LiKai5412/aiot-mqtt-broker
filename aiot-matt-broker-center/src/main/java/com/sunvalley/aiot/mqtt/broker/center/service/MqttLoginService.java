package com.sunvalley.aiot.mqtt.broker.center.service;

import com.sunvalley.aiot.mqtt.broker.center.bean.mqtt.MqttLogin;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 11:48
 * @Desc: 登录请求service
 */
public interface MqttLoginService {

    void mqttLogin(MqttLogin mqttLogin);
}
