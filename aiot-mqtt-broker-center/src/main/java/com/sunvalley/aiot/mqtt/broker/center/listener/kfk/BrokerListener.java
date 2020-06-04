package com.sunvalley.aiot.mqtt.broker.center.listener.kfk;

import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttMessageBo;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;

/**
 * @author kai.li
 * @date 2020/5/19
 */
@Slf4j
public class BrokerListener {

    private TopicManager topicManager;

    public static final String IOT_GET = "iot/get/";

    public BrokerListener(TopicManager topicManager) {
        this.topicManager = topicManager;
    }

    @KafkaListener(topics = "${mqtt.kafka.subscribe-topic}")
    public void receiveCommand(MqttMessageBo mqttMessageBo) {
        if (StringUtils.isEmpty(mqttMessageBo.getSn()) || StringUtils.isEmpty(mqttMessageBo.getProductKey()) || mqttMessageBo.getPayload() == null) {
            log.error("The mqttMessage contains an empty parameter(sn {}, productKey {}, payLoad {})", mqttMessageBo.getSn()
                    , mqttMessageBo.getProductKey(), mqttMessageBo.getPayload());
        }
        String mqttTopic = generateMqttTopic(mqttMessageBo);
        topicManager.getConnectionsByTopic(mqttTopic).forEach(connection -> {
            connection.sendPublishMessage(MqttQoS.AT_MOST_ONCE, false, mqttTopic, mqttMessageBo.getPayLoadStr().getBytes()).subscribe();
        });
    }

    private String generateMqttTopic(MqttMessageBo mqttMessageBo) {
        @NotBlank String productKey = mqttMessageBo.getProductKey();
        @NotBlank String sn = mqttMessageBo.getSn();
        return IOT_GET + productKey + "/" + sn;
    }
}
