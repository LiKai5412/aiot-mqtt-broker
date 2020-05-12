package com.sunvalley.aiot.mqtt.broker.center.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.center.metric.MqttMetric;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttJsonBo;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttPublishBo;
import com.sunvalley.aiot.mqtt.broker.client.constant.KafKaTopicConst;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.MessageType;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.Method;
import com.sunvalley.aiot.mqtt.broker.event.ConnEvent;
import com.sunvalley.aiot.mqtt.broker.event.listener.ConnEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import static com.sunvalley.aiot.mqtt.broker.client.enumeration.LinkStatus.ONLINE;

/**
 * @author kai.li
 * @date 2020/4/24
 */
public class MqttConnEventListener extends ConnEventListener {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void onApplicationEvent(ConnEvent connEvent) {
        //发送kafka
        MqttConnection connection = MqttConnection.class.cast(connEvent.getSource());
        String sn = connection.getSn();
        MqttJsonBo mqttJsonBo = MqttJsonBo.builder().method(Method.UPDATE)
                .state(MqttJsonBo.State.builder().build().addReportedStatus("online", ONLINE.value())).build();
        MqttPublishBo model = MqttPublishBo.builder().sn(sn).messageType(MessageType.JSON).payload(mqttJsonBo).build();
        MqttMetric.incrementTotalConnectionCount();
        //kafkaTemplate.send(KafKaTopicConst.publishTopic, model);
    }
}
