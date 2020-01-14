package com.sunvalley.aiot.mqtt.broker.center.service.impl;

import com.sunvalley.aiot.mqtt.broker.center.bean.event.EventLogin;
import com.sunvalley.aiot.mqtt.broker.center.bean.mqtt.MqttLogin;
import com.sunvalley.aiot.mqtt.broker.center.service.MqttLoginService;
import com.sunvalley.aiot.mqtt.broker.center.service.eventbus.MqttLoginEventBus;
import com.sunvalley.aiot.mqtt.broker.center.service.internal.KafKaClientDelegate;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttLoginBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 10:37
 * @Desc: 登录过程服务
 */
@Slf4j
@Service
@Validated
public class MqttLoginServiceImpl implements MqttLoginService {


    @Autowired
    private MqttLoginEventBus mqttLoginBroker;

    @Autowired
    private KafKaClientDelegate kafKaClient;

    @Value("${broker.topic.login}")
    private String topicLogin;

    @Override
    public void mqttLogin(MqttLogin mqttLogin) {
        //todo
        // 访问策略(黑白名单)

        // 登录事件广播
        mqttLoginBroker.broadMessage(new EventLogin());
        // kfk  入库
        kafKaClient.producerMessage(topicLogin, new MqttLoginBo());
    }
}
