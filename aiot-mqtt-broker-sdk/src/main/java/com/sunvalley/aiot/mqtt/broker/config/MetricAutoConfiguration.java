package com.sunvalley.aiot.mqtt.broker.config;

import com.sunvalley.aiot.mqtt.broker.metric.MqttMetric;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kai.li
 * @date 2020/6/1
 */
@Configuration
public class MetricAutoConfiguration {

    @ConditionalOnWebApplication
    @Bean
    public MqttMetric mqttMetric(){
        return new MqttMetric();
    }
}
