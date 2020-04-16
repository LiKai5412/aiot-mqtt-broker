package com.sunvalley.aiot.mqtt.broker.config;

import com.sunvalley.aiot.mqtt.broker.api.*;
import com.sunvalley.aiot.mqtt.broker.api.cluster.ClusterManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.NonClusterManager;
import com.sunvalley.aiot.mqtt.broker.event.pulisher.MqttEventPublisher;
import com.sunvalley.aiot.mqtt.broker.protocol.ProtocolProcessor;
import com.sunvalley.aiot.mqtt.broker.transport.MqttTcpServer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kai.li
 */
@Configuration
@EnableConfigurationProperties(MqttTcpServerProperties.class)
@AutoConfigureBefore(ProtocolAutoConfiguration.class)
public class MqttTcpServerAutoConfiguration{

    @Bean
    @ConditionalOnMissingBean
    public ConnectionSubscriber connectionSubscriber
            (MqttTcpServerProperties mqttTcpServerProperties, TopicManager topicManager, ProtocolProcessor protocolProcessor, MqttEventPublisher mqttEventPublisher) {
        ConnectionSubscriber connectionSubscriber = new ConnectionSubscriber(topicManager, protocolProcessor, mqttTcpServerProperties, mqttEventPublisher);
        return connectionSubscriber;
    }

    @Bean
    @ConditionalOnMissingBean
    public TopicManager topicManager() {
        return new MemoryTopicManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public ChannelManager channelManager() {
        return new MemoryChannelManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageManager sessionManager() {
        return new MemoryMessageManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public MqttTcpServer mqttAiotTcpServer(MqttTcpServerProperties mqttTcpServerProperties, TopicManager topicManager,
                                           ChannelManager channelManager, ConnectionSubscriber connectionSubscriber, MessageManager messageManager) {
        return new MqttTcpServer(mqttTcpServerProperties, topicManager, channelManager, connectionSubscriber, messageManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public ClusterManager clusterManager() {
        return new NonClusterManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public MqttEventPublisher mqttEventPublisher(){
        return new MqttEventPublisher();
    }
}
