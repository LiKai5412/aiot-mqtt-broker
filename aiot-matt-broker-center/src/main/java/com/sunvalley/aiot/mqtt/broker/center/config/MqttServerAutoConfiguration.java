package com.sunvalley.aiot.mqtt.broker.center.config;

import com.sunvalley.aiot.mqtt.broker.center.service.MqttServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kai.Li
 * @date 2020/3/20
 */
@Configuration
public class MqttServerAutoConfiguration {
    @Bean
    public MqttServer mqttServer(){
        return new MqttServer();
    }
}
