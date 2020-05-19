package com.sunvalley.aiot.mqtt.broker.center.config;

import com.sunvalley.aiot.mqtt.broker.api.ChannelManager;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.ClusterManager;
import com.sunvalley.aiot.mqtt.broker.center.service.ProxyKafkaClusterManager;
import com.sunvalley.aiot.mqtt.broker.config.MqttKafkaTopicProperties;
import com.sunvalley.aiot.mqtt.broker.config.MqttTcpServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author kai.li
 * @date 2020/5/18
 */
@Configuration
public class ClusterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ClusterManager clusterManager(MqttTcpServerProperties mqttTcpServerProperties, KafkaTemplate<String, Object> kafkaTemplate,
                                         ChannelManager channelManager, TopicManager topicManager, MqttKafkaTopicProperties mqttKafkaTopicProperties) {
        return new ProxyKafkaClusterManager(mqttTcpServerProperties, kafkaTemplate, channelManager, topicManager, mqttKafkaTopicProperties);
    }
}
