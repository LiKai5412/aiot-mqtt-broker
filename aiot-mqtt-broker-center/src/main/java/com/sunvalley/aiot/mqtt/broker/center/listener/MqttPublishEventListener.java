package com.sunvalley.aiot.mqtt.broker.center.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.center.metric.MqttMetric;
import com.sunvalley.aiot.mqtt.broker.event.PublishEvent;
import com.sunvalley.aiot.mqtt.broker.event.listener.PublishEventListener;
import io.netty.buffer.ByteBufUtil;

/**
 * @author kai.li
 * @date 2020/4/24
 */
public class MqttPublishEventListener extends PublishEventListener {


    @Override
    public void onApplicationEvent(PublishEvent publishEvent) {
        //统计发送数据
        MqttMetric.incrementTotalPublishCount();
        Long publishBytes = publishEvent.getByteLength();
        MqttMetric.addPublishBytes(publishBytes);
        MqttConnection connection = MqttConnection.class.cast(publishEvent.getSource());
        String sn = connection.getSn();
        MqttMetric.addPublishBytesBySn(sn, publishBytes);
    }
}
