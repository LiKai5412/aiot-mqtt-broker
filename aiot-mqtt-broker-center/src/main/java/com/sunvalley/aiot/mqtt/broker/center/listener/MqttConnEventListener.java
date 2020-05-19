package com.sunvalley.aiot.mqtt.broker.center.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.center.metric.MqttMetric;
import com.sunvalley.aiot.mqtt.broker.client.annotation.KafkaPublishTopic;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttJsonBo;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttMessageBo;
import com.sunvalley.aiot.mqtt.broker.client.constant.KafKaTopicConst;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.MessageType;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.Method;
import com.sunvalley.aiot.mqtt.broker.config.MqttTcpServerProperties;
import com.sunvalley.aiot.mqtt.broker.event.ConnEvent;
import com.sunvalley.aiot.mqtt.broker.event.listener.ConnEventListener;
import com.sunvalley.otter.framework.core.utils.UtilDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Map;

import static com.sunvalley.aiot.mqtt.broker.client.enumeration.LinkStatus.ONLINE;

/**
 * @author kai.li
 * @date 2020/4/24
 */
public class MqttConnEventListener extends ConnEventListener {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaPublishTopic
    private String kafkaPublishTopic;

    @Override
    public void onApplicationEvent(ConnEvent connEvent) {
        //发送kafka
        MqttConnection connection = MqttConnection.class.cast(connEvent.getSource());
        String sn = connection.getSn();
        Long timestamp = UtilDate.toMilliseconds(LocalDateTime.now());
        MqttJsonBo mqttJsonBo = MqttJsonBo.builder().method(Method.UPDATE)
                .state(Map.of(ONLINE.name(), ONLINE.value()))
                .metaData(MqttJsonBo.MetaData.builder().build().addStateMetaData(ONLINE.name(), "timestamp", timestamp))
                .timestamp(timestamp).build();
        MqttMessageBo model = MqttMessageBo.builder().sn(sn).messageType(MessageType.JSON).payload(mqttJsonBo).build();
        MqttMetric.incrementTotalConnectionCount();
        kafkaTemplate.send(kafkaPublishTopic, model);
    }
}
