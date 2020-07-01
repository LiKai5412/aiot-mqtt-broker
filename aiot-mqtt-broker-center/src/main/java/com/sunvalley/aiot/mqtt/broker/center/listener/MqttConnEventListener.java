package com.sunvalley.aiot.mqtt.broker.center.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.client.annotation.KafkaPublishTopic;
import com.sunvalley.aiot.mqtt.broker.client.domain.kfk.MqttJsonBo;
import com.sunvalley.aiot.mqtt.broker.client.domain.kfk.MqttMessageBo;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.MessageType;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.Method;
import com.sunvalley.aiot.mqtt.broker.event.ConnEvent;
import com.sunvalley.aiot.mqtt.broker.event.listener.ConnEventListener;
import com.sunvalley.aiot.mqtt.broker.metric.MqttMetric;
import com.sunvalley.aiot.mqtt.broker.utils.AttributeKeys;
import com.sunvalley.otter.framework.core.utils.UtilDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Map;

import static com.sunvalley.aiot.mqtt.broker.client.enumeration.LinkStatus.X_ONLINE;

/**
 * @author kai.li
 * @date 2020/4/24
 */
public class MqttConnEventListener extends ConnEventListener {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${mqtt.tcp-server.pressure:false}")
    private boolean pressure;

    @KafkaPublishTopic
    private String kafkaPublishTopic;

    @Override
    public void onApplicationEvent(ConnEvent connEvent) {
        //发送kafka
        MqttConnection connection = MqttConnection.class.cast(connEvent.getSource());
        String sn = connection.getAttr(AttributeKeys.DEVICE_ID);
        String vsn = connection.getAttr(AttributeKeys.V_SN);
        Long timestamp = UtilDate.toMilliseconds(LocalDateTime.now());
        String productKey = connection.getConnection().channel().attr(AttributeKeys.PRODUCT_KEY).get();
        MqttJsonBo mqttJsonBo = MqttJsonBo.builder().method(Method.UPDATE.getValue())
                .state(Map.of(X_ONLINE.name(), X_ONLINE.value()))
                .metaData(MqttJsonBo.MetaData.builder().build().addStateMetaData(X_ONLINE.name(), "timestamp", timestamp)).build();
        MqttMessageBo model = MqttMessageBo.builder().productKey(productKey).sn(sn).vsn(vsn).messageType(MessageType.JSON).payload(mqttJsonBo).build();
        MqttMetric.incrementTotalConnectionCount();
        if(!pressure) {
            kafkaTemplate.send(kafkaPublishTopic, model);
        }
    }
}
