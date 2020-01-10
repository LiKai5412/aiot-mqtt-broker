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
     * 异步核心线程数，默认：2
     */
    private int maxProcess = 2;
    /**
     * 异步最大线程数，默认：50
     */
    private int serverPort = 43011;

}
