package com.sunvalley.aiot.mqtt.broker.center.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.client.annotation.KafkaPublishTopic;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttJsonBo;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttMessageBo;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.MessageType;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.Method;
import com.sunvalley.aiot.mqtt.broker.event.IdleEvent;
import com.sunvalley.aiot.mqtt.broker.event.listener.IdleEventListener;
import com.sunvalley.otter.framework.core.utils.UtilDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Map;

import static com.sunvalley.aiot.mqtt.broker.client.enumeration.LinkStatus.X_OFFLINE;

/**
 * @author kai.li
 * @date 2020/4/24
 */
public class MqttIdleEventListener extends IdleEventListener {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaPublishTopic
    private String kafkaPublishTopic;

    @Override
    public void onApplicationEvent(IdleEvent idleEvent) {
        //发送kafka
        MqttConnection connection = MqttConnection.class.cast(idleEvent.getSource());
        String sn = connection.getSn();
        Long timestamp = UtilDate.toMilliseconds(LocalDateTime.now());
        MqttJsonBo mqttJsonBo = MqttJsonBo.builder().method(Method.UPDATE)
                .state(Map.of(X_OFFLINE.name(), X_OFFLINE.value()))
                .metaData(MqttJsonBo.MetaData.builder().build().addStateMetaData(X_OFFLINE.name(), "timestamp", timestamp)).build();
        MqttMessageBo model = MqttMessageBo.builder().sn(sn).messageType(MessageType.JSON).payload(mqttJsonBo).build();
        kafkaTemplate.send(kafkaPublishTopic, model);
    }
}
