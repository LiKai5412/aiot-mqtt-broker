package com.sunvalley.aiot.mqtt.broker.api;

import com.sunvalley.aiot.mqtt.broker.common.message.RetainMessage;

import java.util.Optional;

/**
 * @author kai.li
 * @date 2020/1/14
 */
public interface MessageManager {
    void saveRetainMessage(String topicName, boolean dup, int qos, byte[] copyByteBuf);

    void removeRetainMessage(String topicName);

    Optional<RetainMessage> getRetainMessage(String topicName);
}
