package com.sunvalley.aiot.mqtt.broker.client.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kai.li
 * @date 2020/5/19
 */
@Configuration
public class MqttKafkaTopicAutoConfiguration {

    @Bean
    public EnvironmentAware mqttKafkaTopicEnvironment() {
        return new MqttKafkaTopicEnvironment();
    }
}
