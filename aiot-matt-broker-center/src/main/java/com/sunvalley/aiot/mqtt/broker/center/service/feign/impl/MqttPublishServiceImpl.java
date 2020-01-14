package com.sunvalley.aiot.mqtt.broker.center.service.feign.impl;

import com.sunvalley.aiot.mqtt.broker.center.bean.event.EventPublish;
import com.sunvalley.aiot.mqtt.broker.center.service.broker.MqttPublishBroker;
import com.sunvalley.aiot.mqtt.broker.client.bean.MqttPublishBo;
import com.sunvalley.aiot.mqtt.broker.client.service.MqttPublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 10:37
 * @Desc: mqtt-broker往设备端 发送(下行)消息
 */
@Slf4j
@Validated
@RestController
@RequestMapping("rmi/mqtt-broker/publish")
public class MqttPublishServiceImpl implements MqttPublishService {

    @Autowired
    private MqttPublishBroker mqttPublishBroker;

    @Override
    @PostMapping("pub")
    public void publish(@Validated MqttPublishBo publish) {
        //todo
        // warp
        mqttPublishBroker.broadMessage(new EventPublish());
    }
}
