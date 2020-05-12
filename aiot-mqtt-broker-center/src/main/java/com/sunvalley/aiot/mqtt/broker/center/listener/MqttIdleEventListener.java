package com.sunvalley.aiot.mqtt.broker.center.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttJsonBo;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttPublishBo;
import com.sunvalley.aiot.mqtt.broker.client.constant.KafKaTopicConst;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.MessageType;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.Method;
import com.sunvalley.aiot.mqtt.broker.event.DisConnEvent;
import com.sunvalley.aiot.mqtt.broker.event.IdleEvent;
import com.sunvalley.aiot.mqtt.broker.event.listener.DisConnEventListener;
import com.sunvalley.aiot.mqtt.broker.event.listener.IdleEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import static com.sunvalley.aiot.mqtt.broker.client.enumeration.LinkStatus.OFFLINE;

/**
 * @author kai.li
 * @date 2020/4/24
 */
public class MqttIdleEventListener extends IdleEventListener {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void onApplicationEvent(IdleEvent idleEvent) {
        //发送kafka
        MqttConnection connection = MqttConnection.class.cast(idleEvent.getSource());
        String sn = connection.getSn();
        MqttJsonBo mqttJsonBo = MqttJsonBo.builder().method(Method.UPDATE)
                .state(MqttJsonBo.State.builder().build().addReportedStatus("online", OFFLINE.value())).build();
        MqttPublishBo model = MqttPublishBo.builder().sn(sn).messageType(MessageType.JSON).payload(mqttJsonBo).build();
        //kafkaTemplate.send(KafKaTopicConst.publishTopic, model);
    }

    public static void main(String[] args) {
        System.out.println("0||||data".split("\\|\\|").length);
    }
}
