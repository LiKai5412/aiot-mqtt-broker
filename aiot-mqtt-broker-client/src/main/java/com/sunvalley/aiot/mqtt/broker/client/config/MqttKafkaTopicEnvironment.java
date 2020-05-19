package com.sunvalley.aiot.mqtt.broker.client.config;

import com.sunvalley.aiot.mqtt.broker.client.constant.KafKaTopicConst;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kai.li
 * @date 2020/5/19
 */
public class MqttKafkaTopicEnvironment implements EnvironmentAware {
    private final String DEV_1 = "dev1";

    @Override
    public void setEnvironment(Environment environment) {
        ConfigurableEnvironment env = (ConfigurableEnvironment)environment;
        String[] profiles = env.getActiveProfiles();
        String profile = DEV_1;
        if (profiles != null && profiles.length > 0) {
            profile = profiles[0];
        }
        Map<String, Object> mqttKafkaTopicMap = new HashMap<>(3);
        mqttKafkaTopicMap.put("mqtt.kafka.internal-topic", KafKaTopicConst.INTERNAL_TOPIC.concat("." + profile));
        mqttKafkaTopicMap.put("mqtt.kafka.publish-topic", KafKaTopicConst.PUBLISH_TOPIC.concat("." + profile));
        mqttKafkaTopicMap.put("mqtt.kafka.subscribe-topic", KafKaTopicConst.SUBSCRIBE_TOPIC.concat("." + profile));
        MapPropertySource mapPropertySource = new MapPropertySource("mqttKafkaTopicConfig", mqttKafkaTopicMap);
        env.getPropertySources().addLast(mapPropertySource);
    }

    /*@Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String[] profiles = environment.getActiveProfiles();
        String profile = DEV_1;
        if (profiles != null && profiles.length > 0) {
            profile = profiles[0];
        }
        Map<String, Object> mqttKafkaTopicMap = new HashMap<>(3);
        mqttKafkaTopicMap.put("mqtt.kafka.internal-topic", KafKaTopicConst.INTERNAL_TOPIC.concat("." + profile));
        mqttKafkaTopicMap.put("mqtt.kafka.publish-topic", KafKaTopicConst.PUBLISH_TOPIC.concat("." + profile));
        mqttKafkaTopicMap.put("mqtt.kafka.subscribe-topic", KafKaTopicConst.SUBSCRIBE_TOPIC.concat("." + profile));
        MapPropertySource mapPropertySource = new MapPropertySource("mqttKafkaTopicConfig", mqttKafkaTopicMap);
        environment.getPropertySources().addLast(mapPropertySource);
    }*/
}
