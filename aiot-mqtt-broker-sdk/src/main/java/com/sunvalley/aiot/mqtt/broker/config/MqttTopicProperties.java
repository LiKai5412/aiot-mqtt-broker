package com.sunvalley.aiot.mqtt.broker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * @author kai.li
 * @date 2020/4/30
 */
@Data
@ConfigurationProperties(prefix = "mqtt.topic")
public class MqttTopicProperties {
    private Set<String> prefixPaths;
    private Integer pathDepth = 4;
}
