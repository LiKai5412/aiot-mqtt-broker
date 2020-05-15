package com.sunvalley.aiot.mqtt.broker.api.cluster;

import com.sunvalley.aiot.mqtt.broker.api.ChannelManager;
import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import com.sunvalley.aiot.mqtt.broker.config.MqttTcpServerProperties;
import io.netty.handler.codec.mqtt.MqttQoS;
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
public class KafkaClusterManager extends AbstractClusterManager {

    private KafkaTemplate<String, Object> kafkaTemplate;

    private MqttTcpServerProperties mqttTcpServerProperties;

    private ChannelManager channelManager;

    private TopicManager topicManager;

    public KafkaClusterManager(MqttTcpServerProperties mqttTcpServerProperties, KafkaTemplate<String, Object> kafkaTemplate,
                               ChannelManager channelManager, TopicManager topicManager) {
        this.kafkaTemplate = kafkaTemplate;
        if (mqttTcpServerProperties.getNodeId() == 0) {
            setNodeId("0");
        } else {
            setNodeId(String.valueOf(mqttTcpServerProperties.getNodeId()));
        }
        this.mqttTcpServerProperties = mqttTcpServerProperties;
        this.channelManager = channelManager;
        this.topicManager = topicManager;
    }

    @Override
    @KafkaListener(topics = "${mqtt.tcp-server.kafka-cluster-topic}")
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

    @Override
    void doSend(InternalMessage internalMessage) {
        internalMessage.setNodeId(getNodeId());
        kafkaTemplate.send(mqttTcpServerProperties.getKafkaClusterTopic(), internalMessage);
    }
}
