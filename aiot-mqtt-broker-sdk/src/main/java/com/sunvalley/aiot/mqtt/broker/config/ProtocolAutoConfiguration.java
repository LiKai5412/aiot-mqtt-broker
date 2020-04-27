package com.sunvalley.aiot.mqtt.broker.config;

import com.sunvalley.aiot.mqtt.broker.api.ChannelManager;
import com.sunvalley.aiot.mqtt.broker.api.MessageManager;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.ClusterManager;
import com.sunvalley.aiot.mqtt.broker.common.auth.IAuthService;
import com.sunvalley.aiot.mqtt.broker.event.pulisher.MqttEventPublisher;
import com.sunvalley.aiot.mqtt.broker.protocol.ProtocolProcessor;
import com.sunvalley.aiot.mqtt.broker.protocol.mqtt.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kai.Li
 * @date 2020/3/12
 */
@Configuration
public class ProtocolAutoConfiguration {

    private final ChannelManager channelManager;
    private final IAuthService authService;
    private final MessageManager messageManager;
    private final TopicManager topicManager;
    private final ClusterManager clusterManager;

    public ProtocolAutoConfiguration(ChannelManager channelManager, IAuthService authService, MessageManager messageManager, TopicManager topicManager, ClusterManager clusterManager) {
        this.channelManager = channelManager;
        this.authService = authService;
        this.messageManager = messageManager;
        this.topicManager = topicManager;
        this.clusterManager = clusterManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public ProtocolProcessor protocolProcess(Connect connect, DisConnect disConnect, PingResp pingResp, Publish publish, PubAck pubAck,
                                             PubRec pubRec, PubRel pubRel, PubComp pubComp, Subscribe subscribe, UnSubscribe unSubscribe) {
        ProtocolProcessor protocolProcessor = new ProtocolProcessor();
        PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        propertyMapper.from(authService).to(protocolProcessor::setAuthService);
        propertyMapper.from(channelManager).to(protocolProcessor::setChannelManager);
        propertyMapper.from(messageManager).to(protocolProcessor::setMessageManager);
        propertyMapper.from(topicManager).to(protocolProcessor::setTopicManager);
        propertyMapper.from(connect).to(protocolProcessor::setConnect);
        propertyMapper.from(disConnect).to(protocolProcessor::setDisConnect);
        propertyMapper.from(pingResp).to(protocolProcessor::setPingResp);
        propertyMapper.from(publish).to(protocolProcessor::setPublish);
        propertyMapper.from(pubAck).to(protocolProcessor::setPubAck);
        propertyMapper.from(pubRec).to(protocolProcessor::setPubRec);
        propertyMapper.from(pubRel).to(protocolProcessor::setPubRel);
        propertyMapper.from(pubComp).to(protocolProcessor::setPubComp);
        propertyMapper.from(subscribe).to(protocolProcessor::setSubscribe);
        propertyMapper.from(unSubscribe).to(protocolProcessor::setUnSubscribe);
        return protocolProcessor;
    }

    @Bean
    @ConditionalOnMissingBean
    public Connect connect(MqttEventPublisher mqttEventPublisher) {
        return new Connect(authService, channelManager, clusterManager, topicManager, mqttEventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public DisConnect disConnect() {
        return new DisConnect();
    }

    @Bean
    @ConditionalOnMissingBean
    public PingResp pingResp() {
        return new PingResp();
    }

    @Bean
    @ConditionalOnMissingBean
    public Publish publish(MqttEventPublisher mqttEventPublisher) {
        return new Publish(messageManager, topicManager, clusterManager, mqttEventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public PubAck pubAck() {
        return new PubAck();
    }

    @Bean
    @ConditionalOnMissingBean
    public PubRec pubRec() {
        return new PubRec();
    }

    @Bean
    @ConditionalOnMissingBean
    public PubRel pubRel() {
        return new PubRel(topicManager, clusterManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public PubComp pubComp() {
        return new PubComp();
    }

    @Bean
    @ConditionalOnMissingBean
    public Subscribe subscribe() {
        return new Subscribe(topicManager, messageManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public UnSubscribe unSubscribe() {
        return new UnSubscribe(topicManager);
    }
}
