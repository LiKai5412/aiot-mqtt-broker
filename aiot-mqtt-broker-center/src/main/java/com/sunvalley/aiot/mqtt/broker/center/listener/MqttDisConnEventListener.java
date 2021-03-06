package com.sunvalley.aiot.mqtt.broker.center.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.client.annotation.KafkaPublishTopic;
import com.sunvalley.aiot.mqtt.broker.client.domain.kfk.MqttJsonBo;
import com.sunvalley.aiot.mqtt.broker.client.domain.kfk.MqttMessageBo;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.MessageType;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.Method;
import com.sunvalley.aiot.mqtt.broker.event.DisConnEvent;
import com.sunvalley.aiot.mqtt.broker.event.listener.DisConnEventListener;
import com.sunvalley.aiot.mqtt.broker.metric.MqttMetric;
import com.sunvalley.aiot.mqtt.broker.utils.AttributeKeys;
import com.sunvalley.otter.framework.core.utils.UtilDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static com.sunvalley.aiot.mqtt.broker.client.enumeration.LinkStatus.X_OFFLINE;

/**
 * @author kai.li
 * @date 2020/4/24
 */
public class MqttDisConnEventListener extends DisConnEventListener {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaPublishTopic
    private String kafkaPublishTopic;

    @Value("${mqtt.tcp-server.pressure:false}")
    private boolean pressure;

    @Override
    public void onApplicationEvent(DisConnEvent disConnEvent) {
        //发送kafka
        MqttConnection connection = MqttConnection.class.cast(disConnEvent.getSource());
        String sn = connection.getAttr(AttributeKeys.DEVICE_ID);
        String vsn = connection.getAttr(AttributeKeys.V_SN);
        //如果没有sn则忽略此消息
        if (StringUtils.isEmpty(sn)) {
            return;
        }
        Long timestamp = UtilDate.toMilliseconds(LocalDateTime.now());
        String productKey = connection.getConnection().channel().attr(AttributeKeys.PRODUCT_KEY).get();
        MqttJsonBo mqttJsonBo = MqttJsonBo.builder().method(Method.UPDATE.getValue())
                .state(Map.of(X_OFFLINE.name(), X_OFFLINE.value()))
                .metaData(MqttJsonBo.MetaData.builder().build().addStateMetaData(X_OFFLINE.name(), "timestamp", timestamp)).build();
        MqttMessageBo model = MqttMessageBo.builder().productKey(productKey).sn(sn).vsn(vsn).messageType(MessageType.JSON).payload(mqttJsonBo).build();
        MqttMetric.decrementTotalConnectionCount();
        MqttMetric.removePublishBytesBySn(sn);
        if(!pressure) {
            kafkaTemplate.send(kafkaPublishTopic, model);
        }
    }
}
