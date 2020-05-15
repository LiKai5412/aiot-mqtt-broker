package com.sunvalley.aiot.mqtt.broker.center.config;

import com.sunvalley.aiot.mqtt.broker.api.ChannelManager;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.ClusterManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.KafkaClusterManager;
import com.sunvalley.aiot.mqtt.broker.center.metric.MqttMetric;
import com.sunvalley.aiot.mqtt.broker.center.service.MqttServer;
import com.sunvalley.aiot.mqtt.broker.config.MqttTcpServerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

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

    @Bean
    public MqttMetric mqttMetric(){
        return new MqttMetric();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClusterManager clusterManager(MqttTcpServerProperties mqttTcpServerProperties,
                                         KafkaTemplate<String, Object> kafkaTemplate, ChannelManager channelManager, TopicManager topicManager) {
        return new KafkaClusterManager(mqttTcpServerProperties, kafkaTemplate, channelManager, topicManager);
    }
}
