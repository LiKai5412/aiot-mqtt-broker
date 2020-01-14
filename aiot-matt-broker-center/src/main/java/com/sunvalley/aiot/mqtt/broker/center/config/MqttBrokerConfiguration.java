package com.sunvalley.aiot.mqtt.broker.center.config;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/10 14:33
 * @Desc: mqtt broker
 */
@Configuration
@EnableConfigurationProperties(MqttBrokerProperties.class)
public class MqttBrokerConfiguration {

    @Autowired
    private MqttBrokerProperties mqttBrokerProperties;



}
