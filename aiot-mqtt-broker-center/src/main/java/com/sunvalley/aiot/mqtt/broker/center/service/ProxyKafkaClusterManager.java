package com.sunvalley.aiot.mqtt.broker.center.service;

import com.sunvalley.aiot.mqtt.broker.api.ChannelManager;
import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.KafkaClusterManager;
import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import com.sunvalley.aiot.mqtt.broker.config.MqttKafkaTopicProperties;
import com.sunvalley.aiot.mqtt.broker.config.MqttTcpServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author kai.li
 * @date 2020/5/18
 */
@Slf4j
public class ProxyKafkaClusterManager extends KafkaClusterManager {

    public ProxyKafkaClusterManager(MqttTcpServerProperties mqttTcpServerProperties, KafkaTemplate<String, Object> kafkaTemplate,
                                    ChannelManager channelManager, TopicManager topicManager, MqttKafkaTopicProperties mqttKafkaTopicProperties) {
        super(mqttTcpServerProperties, kafkaTemplate, channelManager, topicManager, mqttKafkaTopicProperties);
    }

    @Override
    @KafkaListener(topics = "${mqtt.kafka.internal-topic}")
    public void receiveInternalMessage(InternalMessage internalMessage) {
        if (getNodeId().equalsIgnoreCase(internalMessage.getNodeId())) {
            return;
        }
        switch (internalMessage.getMessageType()) {
            case CONNMESSAGE:
                MqttConnection mqttConnection = getChannelManager().getConnectionByDeviceId(internalMessage.getDeviceId());
                if (mqttConnection != null) {
                    //断开重复deviceId的客户端
                    log.debug("Disconnect deviceId {} due to relogin", internalMessage.getDeviceId());
                    mqttConnection.dispose();
                }
                break;
            default:
        }
    }

    @Override
    protected void doSend(InternalMessage internalMessage) {
        switch (internalMessage.getMessageType()) {
            case CONNMESSAGE:
                super.doSend(internalMessage);
                break;
            default:
        }
    }
}
