package com.sunvalley.aiot.mqtt.broker.center.service;

import com.sunvalley.aiot.mqtt.broker.api.ChannelManager;
import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.KafkaClusterManager;
import com.sunvalley.aiot.mqtt.broker.client.domain.kfk.MqttMessageBo;
import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import com.sunvalley.aiot.mqtt.broker.config.MqttKafkaTopicProperties;
import com.sunvalley.aiot.mqtt.broker.config.MqttTcpServerProperties;
import com.sunvalley.aiot.mqtt.broker.metric.MqttMetric;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author kai.li
 * @date 2020/5/18
 */
@Slf4j
public class ProxyKafkaClusterManager extends KafkaClusterManager {

    public static final String IOT_GET = "iot/get/";

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
            case RESPMESSAE:
                //将消息转发至指定客户端
                List<MqttConnection> mqttConnections = getTopicManager().getConnectionsByTopic(internalMessage.getTopic());
                if (!CollectionUtils.isEmpty(mqttConnections)) {
                    mqttConnections.forEach(connection -> connection.sendPublishMessage(MqttQoS.valueOf(internalMessage.getMqttQoS()),
                            internalMessage.isRetain(), internalMessage.getTopic(), internalMessage.getMessageBytes()).subscribe());
                }
                break;
            default:
        }
    }

    /**
     * 接收返回消息
     * @param mqttMessageBo
     */
    @KafkaListener(topics = "${mqtt.kafka.subscribe-topic}")
    public void receiveCommand(MqttMessageBo mqttMessageBo) {
        if (StringUtils.isEmpty(mqttMessageBo.getSn()) || StringUtils.isEmpty(mqttMessageBo.getProductKey()) || mqttMessageBo.getPayload() == null) {
            log.error("The mqttMessage contains an empty parameter(sn {}, productKey {}, payLoad {})", mqttMessageBo.getSn(),
                    mqttMessageBo.getProductKey(), mqttMessageBo.getPayload());
        }
        String mqttTopic = generateMqttTopic(mqttMessageBo);
        MqttMetric.incrementTotalPublishCount();
        MqttMetric.addPublishBytes(mqttMessageBo.getPayLoadStr().getBytes().length);
        MqttMetric.addPublishBytesBySn(mqttMessageBo.getSn(), mqttMessageBo.getPayLoadStr().getBytes().length);
        getTopicManager().getConnectionsByTopic(mqttTopic).forEach(connection -> {
            connection.sendPublishMessage(MqttQoS.AT_MOST_ONCE, false, mqttTopic, mqttMessageBo.getPayLoadStr().getBytes()).subscribe();
        });
    }

    private String generateMqttTopic(MqttMessageBo mqttMessageBo) {
        @NotBlank String productKey = mqttMessageBo.getProductKey();
        @NotBlank String sn = mqttMessageBo.getSn();
        return IOT_GET + productKey + "/" + sn;
    }

    @Override
    protected void doSend(InternalMessage internalMessage) {
        switch (internalMessage.getMessageType()) {
            case RESPMESSAE:
            case CONNMESSAGE:
                getKafkaTemplate().send(getMqttKafkaTopicProperties().getInternalTopic(), internalMessage);
                break;
            default:
        }
    }
}
