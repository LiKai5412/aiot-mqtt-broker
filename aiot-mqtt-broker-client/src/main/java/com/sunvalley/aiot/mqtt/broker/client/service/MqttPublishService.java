package com.sunvalley.aiot.mqtt.broker.client.service;

import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttMessageBo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 10:41
 * @Desc: mqtt-broker往设备端 发送(下行)消息
 */
@RequestMapping("rmi/mqtt-broker/publish")
public interface MqttPublishService {

    @PostMapping("pub")
    void publish(@Validated MqttMessageBo publish);
}
