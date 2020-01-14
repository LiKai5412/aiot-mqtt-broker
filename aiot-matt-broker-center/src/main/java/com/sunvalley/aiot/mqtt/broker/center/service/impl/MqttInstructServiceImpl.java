package com.sunvalley.aiot.mqtt.broker.center.service.impl;

import com.sunvalley.aiot.mqtt.broker.center.bean.mqtt.MqttInstruct;
import com.sunvalley.aiot.mqtt.broker.center.service.MqttInstructService;
import com.sunvalley.aiot.mqtt.broker.center.service.internal.KafKaClientDelegate;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttInstructBo;
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
public class MqttInstructServiceImpl implements MqttInstructService {

    @Autowired
    private KafKaClientDelegate kafKaClient;

    @Value("${broker.topic.instruct}")
    private String topicInstruct;

    @Override
    public void mqttInstruct(MqttInstruct mqttInstruct) {
        //todo
        // 初步清洗、结构化
        // kfk
        kafKaClient.producerMessage(topicInstruct, new MqttInstructBo());
    }
}
