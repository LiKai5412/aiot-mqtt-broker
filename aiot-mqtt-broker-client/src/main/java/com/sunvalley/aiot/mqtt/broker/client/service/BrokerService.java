package com.sunvalley.aiot.mqtt.broker.client.service;

import com.sunvalley.aiot.mqtt.broker.client.annotation.KafkaInternalTopic;
import com.sunvalley.aiot.mqtt.broker.client.bean.po.DisconnMessage;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author kai.li
 * @date 2020/5/25
 */
public class BrokerService {

    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaInternalTopic
    private String kafkaInternalTopic;

    public BrokerService(KafkaTemplate kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 断开指定设备mqtt连接
     * @param sn
     */
    public void disconnDeviceBySn(String sn){
        DisconnMessage disconnMessage = DisconnMessage.buildConnMessage(sn);
        kafkaTemplate.send(kafkaInternalTopic, disconnMessage);
    }
}
