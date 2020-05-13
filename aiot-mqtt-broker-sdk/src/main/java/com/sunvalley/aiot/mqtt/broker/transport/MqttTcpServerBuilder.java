package com.sunvalley.aiot.mqtt.broker.transport;

import com.sunvalley.aiot.mqtt.broker.api.*;
import com.sunvalley.aiot.mqtt.broker.api.cluster.ClusterManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.NonClusterManager;
import com.sunvalley.aiot.mqtt.broker.common.auth.IAuthService;
import com.sunvalley.aiot.mqtt.broker.config.MqttTcpServerProperties;
import com.sunvalley.aiot.mqtt.broker.config.MqttTopicProperties;
import com.sunvalley.aiot.mqtt.broker.event.pulisher.MqttEventPublisher;
import com.sunvalley.aiot.mqtt.broker.protocol.ProtocolProcessor;
import lombok.Data;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author kai.li
 */
@Data
public class MqttTcpServerBuilder {

    private MqttTcpServerProperties mqttTcpServerProperties;
    private TopicManager topicManager;
    private ChannelManager channelManager;
    private ProtocolProcessor protocolProcessor;
    private ConnectionSubscriber connectionSubscriber;
    private MessageManager messageManager;
    private ClusterManager clusterManager;
    private MqttEventPublisher mqttEventPublisher;
    private MqttTopicProperties mqttTopicProperties;

    private MqttTcpServerBuilder() {
        this.mqttTcpServerProperties = new MqttTcpServerProperties();
    }

    public MqttTcpServerBuilder(String ip, int port) {
        this();
        mqttTcpServerProperties.setIp(ip);
        mqttTcpServerProperties.setPort(port);
        this.topicManager = new MemoryTopicManager();
        this.channelManager = new MemoryChannelManager();
        this.clusterManager = new NonClusterManager();
        this.messageManager = new MemoryMessageManager();
        this.mqttTopicProperties = new MqttTopicProperties();
        this.protocolProcessor = new ProtocolProcessor((user, pass) -> true,
                channelManager, messageManager, topicManager, clusterManager, mqttEventPublisher, mqttTopicProperties);
        this.mqttEventPublisher = new MqttEventPublisher();
        this.connectionSubscriber = new ConnectionSubscriber(topicManager, protocolProcessor, mqttTcpServerProperties, mqttEventPublisher);
    }

    public MqttTcpServerBuilder heartInSecond(int heart) {
        mqttTcpServerProperties.setHeartInSecond(heart);
        return this;
    }

    public MqttTcpServerBuilder tls(boolean tls) {
        mqttTcpServerProperties.setTls(tls);
        return this;
    }

    public MqttTcpServerBuilder log(boolean log) {
        mqttTcpServerProperties.setPrintLog(log);
        return this;
    }

    public MqttTcpServerBuilder keepAlive(boolean isKeepAlive) {
        mqttTcpServerProperties.setKeepAlive(isKeepAlive);
        return this;
    }

    public MqttTcpServerBuilder noDelay(boolean noDelay) {
        mqttTcpServerProperties.setNoDelay(noDelay);
        return this;
    }

    public MqttTcpServerBuilder backlog(int length) {
        mqttTcpServerProperties.setBacklog(length);
        return this;
    }

    public MqttTcpServerBuilder sendBufSize(int size) {
        mqttTcpServerProperties.setSendBufSize(size);
        return this;
    }

    public MqttTcpServerBuilder revBufSize(int size) {
        mqttTcpServerProperties.setRevBufSize(size);
        return this;
    }


    public MqttTcpServerBuilder auth(IAuthService auth) {
        protocolProcessor.setAuthService(auth);
        return this;
    }

    public MqttTcpServerBuilder topicManager(TopicManager topicManager) {
        this.topicManager = topicManager;
        return this;
    }

    public MqttTcpServerBuilder channelManager(ChannelManager channelManager) {
        this.channelManager = channelManager;
        return this;
    }

    public MqttTcpServerBuilder connectionSubscriber(ConnectionSubscriber connectionSubscriber) {
        this.connectionSubscriber = connectionSubscriber;
        return this;
    }

    //todo
    /*public MqttTcpServerBuilder retainMessageHandler(RsocketMessageHandler messageHandler) {
        Optional.ofNullable(messageHandler)
                .ifPresent(mqttTcpServerProperties::setMessageHandler);
        return this;
    }*/

    public MqttTcpServerBuilder exception(Consumer<Throwable> exceptionConsumer) {
        Optional.ofNullable(exceptionConsumer)
                .ifPresent(mqttTcpServerProperties::setThrowableConsumer);
        return this;
    }

    public MqttTcpServer build() {
        return new MqttTcpServer(mqttTcpServerProperties, topicManager,
                channelManager, connectionSubscriber, messageManager);
    }
}
