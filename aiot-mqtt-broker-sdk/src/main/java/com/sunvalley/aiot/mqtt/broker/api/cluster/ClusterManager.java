package com.sunvalley.aiot.mqtt.broker.api.cluster;

import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;

/**
 * @author kai.li
 */
public interface ClusterManager {
    /**
     *  发送上行集群间内部消息，集群中的每个节点都会受到该消息
     */
    void sendInternalMessage(InternalMessage internalMessage);

    /**
     * 接收上行集群间内部消息
     * @param objs
     */
    void receiveInternalMessage(InternalMessage internalMessage);
}
