package com.sunvalley.aiot.mqtt.broker.api.cluster;

import com.sunvalley.aiot.mqtt.broker.api.ChannelManager;
import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import com.sunvalley.aiot.mqtt.broker.config.MqttKafkaTopicProperties;
import com.sunvalley.aiot.mqtt.broker.config.MqttTcpServerProperties;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author kai.li
 * @date 2020/1/16
 */
@Slf4j
@Data
public class KafkaClusterManager extends AbstractClusterManager {

    private KafkaTemplate<String, Object> kafkaTemplate;

    private MqttTcpServerProperties mqttTcpServerProperties;

    private ChannelManager channelManager;

    private TopicManager topicManager;

    private MqttKafkaTopicProperties mqttKafkaTopicProperties;

    public KafkaClusterManager(MqttTcpServerProperties mqttTcpServerProperties, KafkaTemplate<String, Object> kafkaTemplate,
                               ChannelManager channelManager, TopicManager topicManager, MqttKafkaTopicProperties mqttKafkaTopicProperties) {
        this.kafkaTemplate = kafkaTemplate;
        setNodeId(String.valueOf(mqttTcpServerProperties.getNodeId()));
        this.mqttTcpServerProperties = mqttTcpServerProperties;
        this.channelManager = channelManager;
        this.topicManager = topicManager;
        this.mqttKafkaTopicProperties = mqttKafkaTopicProperties;
    }

    @Override
    @KafkaListener(topics = "${mqtt.kafka.internal-topic}")
    public void receiveInternalMessage(InternalMessage internalMessage) {
        //丢弃自己发送的消息
        if (getNodeId().equalsIgnoreCase(internalMessage.getNodeId())) {
            return;
        }
        switch (internalMessage.getMessageType()) {
            case CONNMESSAGE:
                MqttConnection mqttConnection = channelManager.getConnectionByDeviceId(internalMessage.getDeviceId());
                if (mqttConnection != null) {
                    //断开重复deviceId的客户端
                    log.debug("Disconnect deviceId {} due to relogin ", internalMessage.getDeviceId());
                    mqttConnection.dispose();
                }
                break;
            case PUBMESSAGE:
                //将消息转发至指定客户端
                List<MqttConnection> mqttConnections = topicManager.getConnectionsByTopic(internalMessage.getTopic());
                if (!CollectionUtils.isEmpty(mqttConnections)) {
                    mqttConnections.forEach(connection -> connection.sendPublishMessage(MqttQoS.valueOf(internalMessage.getMqttQoS()),
                            internalMessage.isRetain(), internalMessage.getTopic(), internalMessage.getMessageBytes()).subscribe());
                }
                break;
            default:
        }
    }

    @KafkaListener(topics = "${mqtt.kafka.response-internal-topic}")
    public void receiveResponseInternalMessage(InternalMessage internalMessage) {
        //丢弃自己发送的消息
        if (getNodeId().equalsIgnoreCase(internalMessage.getNodeId())) {
            return;
        }
        switch (internalMessage.getMessageType()) {
            case PUBMESSAGE:
                //将消息转发至指定客户端
                List<MqttConnection> mqttConnections = topicManager.getConnectionsByTopic(internalMessage.getTopic());
                if (!CollectionUtils.isEmpty(mqttConnections)) {
                    mqttConnections.forEach(connection -> connection.sendPublishMessage(MqttQoS.valueOf(internalMessage.getMqttQoS()),
                            internalMessage.isRetain(), internalMessage.getTopic(), internalMessage.getMessageBytes()).subscribe());
                }
                break;
            default:
        }
    }

    @Override
    protected void doSend(InternalMessage internalMessage) {
        internalMessage.setNodeId(getNodeId());
        kafkaTemplate.send(mqttKafkaTopicProperties.getInternalTopic(), internalMessage);
    }
}
