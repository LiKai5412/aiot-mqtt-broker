package com.sunvalley.aiot.mqtt.broker.center.controller;

import com.sunvalley.aiot.mqtt.broker.center.bean.event.EventLogin;
import com.sunvalley.aiot.mqtt.broker.center.service.eventbus.MqttLoginEventBus;
import com.sunvalley.aiot.mqtt.broker.center.service.internal.KafKaProducerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/14 11:49
 * @Desc: 后台测验
 */
@Slf4j
@Validated
@RestController
@RequestMapping("bg/")
public class BgController {


    @Autowired
    private KafKaProducerClient kafKaClient;

    @Autowired
    private MqttLoginEventBus mqttLoginBroker;


    @PostMapping("kfk")
    public String sendKfK(@RequestParam String topic, @RequestParam String message) {
        kafKaClient.producerMessage(topic, Map.of("message", message));
        return "succ";
    }


    @PostMapping("eventLogin")
    public String sendEventLogin(@RequestParam String clientId) {
        EventLogin event = new EventLogin();
        event.setClientId(clientId);
        mqttLoginBroker.broadMessage(event);
        return "succ";
    }
}
