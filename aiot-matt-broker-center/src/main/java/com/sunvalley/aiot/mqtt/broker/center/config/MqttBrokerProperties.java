package com.sunvalley.aiot.mqtt.broker.center.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/10 15:05
 * @Desc: ****
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties("mqtt.broker")
public class MqttBrokerProperties {

    /**
     * mqtt-broker 端口号
     */
    private Integer serverPort;

}
