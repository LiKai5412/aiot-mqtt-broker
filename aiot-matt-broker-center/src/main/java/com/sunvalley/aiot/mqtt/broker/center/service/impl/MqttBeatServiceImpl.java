package com.sunvalley.aiot.mqtt.broker.center.service.impl;

import com.sunvalley.aiot.mqtt.broker.center.bean.mqtt.MqttBeat;
import com.sunvalley.aiot.mqtt.broker.center.service.MqttBeatService;
import com.sunvalley.aiot.mqtt.broker.center.service.internal.KafKaClientDelegate;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttBeatBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 10:37
 * @Desc: 登录过程访问
 */
@Slf4j
@Service
@Validated
public class MqttBeatServiceImpl implements MqttBeatService {

    @Autowired
    private KafKaClientDelegate kafKaClient;

    @Value("${broker.topic.beat}")
    private String topicBeat;

    @Override
    public void mqttBeat(MqttBeat mqttBeat) {
        //todo
        // 初步清洗、结构化、心跳缓存、漏桶？
        // kfk
        kafKaClient.producerMessage(topicBeat, new MqttBeatBo());
    }
}
