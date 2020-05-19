package com.sunvalley.aiot.mqtt.broker.event.listener;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.cluster.ClusterManager;
import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import com.sunvalley.aiot.mqtt.broker.event.ConnEvent;
import com.sunvalley.aiot.mqtt.broker.event.PublishEvent;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import reactor.core.publisher.Mono;

/**
 * @author kai.li
 * @date 2020/2/25
 */
@Slf4j
public class PublishEventListener implements ApplicationListener<PublishEvent> {

    private ClusterManager clusterManager;

    public PublishEventListener(ClusterManager clusterManager){
        this.clusterManager = clusterManager;
    }

    public PublishEventListener(){

    }

    @Override
    public void onApplicationEvent(PublishEvent publishEvent) {
        MqttConnection connection = (MqttConnection) publishEvent.getSource();
        log.debug("{} is going to publish!", connection);
        Mono.just(createInternalMessage(publishEvent.getMsg(), publishEvent.getArray())).subscribe(clusterManager::sendInternalMessage);
    }

    private InternalMessage createInternalMessage(MqttPublishMessage msg, byte[] array) {
        MqttFixedHeader header = msg.fixedHeader();
        MqttPublishVariableHeader variableHeader = msg.variableHeader();
        return InternalMessage.buildPubMessage(variableHeader.topicName(), header.qosLevel().value(),
                array, header.isRetain(), header.isDup());
    }
}
