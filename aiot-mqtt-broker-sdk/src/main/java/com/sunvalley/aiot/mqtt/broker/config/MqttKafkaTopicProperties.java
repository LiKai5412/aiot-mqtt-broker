package com.sunvalley.aiot.mqtt.broker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kai.li
 * @date 2020/5/19
 */
@Data
@ConfigurationProperties(prefix = "mqtt.kafka")
public class MqttKafkaTopicProperties {
    private String internalTopic;

    private String publishTopic;

    private String subscribeTopic;

    private String responseInternalTopic;
}
