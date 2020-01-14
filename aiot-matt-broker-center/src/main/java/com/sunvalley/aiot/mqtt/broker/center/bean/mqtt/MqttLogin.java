package com.sunvalley.aiot.mqtt.broker.center.bean.mqtt;

import lombok.Data;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 11:35
 * @Desc: mqtt 设备登录
 */
@Data
public class MqttLogin {
    private String clientId;
    private String userName;
    private String password;
}
