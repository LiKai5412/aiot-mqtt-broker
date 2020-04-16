package com.sunvalley.aiot.mqtt.broker.api.cluster;

import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author kai.li
 * @date 2020/1/16
 */
public class NonClusterManager extends AbstractClusterManager {

    @Override
    void doSend(InternalMessage internalMessage) {
        return;
    }

    @Override
    @Value("mqtt-broker-node-id:''")
    public void setNodeId(String nodeId) {
        super.setNodeId(nodeId);
    }
}
