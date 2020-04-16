package com.sunvalley.aiot.mqtt.broker.api.cluster;

import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;

/**
 * @author kai.li
 */
public interface ClusterManager {
    /**
     *  发送集群间内部消息
     */
    void sendInternalMessage(InternalMessage internalMessage);

    /**
     * 接收集群间内部消息
     * @param objs
     */
    void receiveInternalMessage(Object ... objects);
}
