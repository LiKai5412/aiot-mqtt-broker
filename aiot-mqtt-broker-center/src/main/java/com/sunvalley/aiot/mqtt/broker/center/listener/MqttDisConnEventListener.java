package com.sunvalley.aiot.mqtt.broker.center.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.center.metric.MqttMetric;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttJsonBo;
import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttPublishBo;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.MessageType;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.Method;
import com.sunvalley.aiot.mqtt.broker.event.DisConnEvent;
import com.sunvalley.aiot.mqtt.broker.event.listener.DisConnEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;

import static com.sunvalley.aiot.mqtt.broker.client.enumeration.LinkStatus.OFFLINE;

/**
 * @author kai.li
 * @date 2020/4/24
 */
public class MqttDisConnEventListener extends DisConnEventListener {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void onApplicationEvent(DisConnEvent disConnEvent) {
        //发送kafka
        MqttConnection connection = MqttConnection.class.cast(disConnEvent.getSource());
        String sn = connection.getSn();
        //如果没有sn则忽略此消息
        if(StringUtils.isEmpty(sn)){
            return;
        }
        MqttJsonBo mqttJsonBo = MqttJsonBo.builder().method(Method.UPDATE)
                .state(MqttJsonBo.State.builder().build().addReportedStatus("online", OFFLINE.value())).build();
        MqttPublishBo model = MqttPublishBo.builder().sn(sn).messageType(MessageType.JSON).payload(mqttJsonBo).build();
        MqttMetric.decrementTotalConnectionCount();
        MqttMetric.removePublishBytesBySn(sn);
        //kafkaTemplate.send(KafKaTopicConst.publishTopic, model);
    }
}
