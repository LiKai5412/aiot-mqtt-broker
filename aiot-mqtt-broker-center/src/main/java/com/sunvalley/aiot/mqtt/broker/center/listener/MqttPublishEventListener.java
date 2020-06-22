package com.sunvalley.aiot.mqtt.broker.center.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.center.util.UtilMessage;
import com.sunvalley.aiot.mqtt.broker.client.annotation.KafkaPublishTopic;
import com.sunvalley.aiot.mqtt.broker.client.domain.kfk.MqttJsonBo;
import com.sunvalley.aiot.mqtt.broker.client.domain.kfk.MqttMessageBo;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.MessageType;
import com.sunvalley.aiot.mqtt.broker.event.PublishEvent;
import com.sunvalley.aiot.mqtt.broker.event.listener.PublishEventListener;
import com.sunvalley.aiot.mqtt.broker.metric.MqttMetric;
import com.sunvalley.aiot.mqtt.broker.utils.AttributeKeys;
import com.sunvalley.otter.framework.core.utils.UtilDate;
import com.sunvalley.otter.framework.core.utils.UtilJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author kai.li
 * @date 2020/4/24
 */
public class MqttPublishEventListener extends PublishEventListener {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaPublishTopic
    private String kafkaPublishTopic;

    public MqttPublishEventListener() {
        super();
    }

    @Override
    public void onApplicationEvent(PublishEvent publishEvent) {
        //统计接收数据
        MqttMetric.incrementTotalReceiveCount();
        Long publishBytes = (long) publishEvent.getArray().length;
        MqttMetric.addReceiveBytes(publishBytes);
        MqttConnection connection = MqttConnection.class.cast(publishEvent.getSource());
        String sn = connection.getAttr(AttributeKeys.DEVICE_ID);
        String vsn = connection.getAttr(AttributeKeys.V_SN);
        String productKey = connection.getConnection().channel().attr(AttributeKeys.PRODUCT_KEY).get();
        Long timestamp = UtilDate.toMilliseconds(LocalDateTime.now());
        MqttMetric.addReceiveBytesBySn(sn, publishBytes);
        MessageType messageType = UtilMessage.getMessageType(publishEvent.getArray());
        Object payLoad = new String(publishEvent.getArray(), StandardCharsets.UTF_8);
        if(messageType == MessageType.JSON){
            payLoad = UtilJson.readValue((String) payLoad, MqttJsonBo.class);
        }
        MqttMessageBo mqttMessageBo = MqttMessageBo.builder().sn(sn).vsn(vsn).productKey(productKey)
                .messageType(messageType).timestamp(timestamp).payload(payLoad).build();
        kafkaTemplate.send(kafkaPublishTopic, mqttMessageBo);
    }
}
