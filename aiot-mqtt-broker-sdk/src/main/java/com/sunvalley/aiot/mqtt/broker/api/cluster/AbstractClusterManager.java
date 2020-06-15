package com.sunvalley.aiot.mqtt.broker.api.cluster;

import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import lombok.Data;

/**
 * @author kai.li
 * @date 2020/1/15
 */
@Data
public abstract class AbstractClusterManager implements ClusterManager {

    protected String nodeId;

    @Override
    public void sendInternalMessage(InternalMessage internalMessage) {
        internalMessage.setNodeId(nodeId);
        doSend(internalMessage);
        return;
    }

    @Override
    public void receiveInternalMessage(InternalMessage internalMessage) {
        return;
    }

    public void receiveResponseInternalMessage(InternalMessage internalMessage) {
        return;
    }

    abstract protected void doSend(InternalMessage internalMessage);
}
