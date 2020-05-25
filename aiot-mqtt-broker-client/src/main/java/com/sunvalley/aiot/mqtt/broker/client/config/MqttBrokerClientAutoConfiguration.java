package com.sunvalley.aiot.mqtt.broker.client.config;

import com.sunvalley.aiot.mqtt.broker.client.service.BrokerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author kai.li
 * @date 2020/5/25
 */
@Configuration
public class MqttBrokerClientAutoConfiguration {

    @Bean
    public BrokerService brokerService(KafkaTemplate kafkaTemplate){
        return new BrokerService(kafkaTemplate);
    }
}
