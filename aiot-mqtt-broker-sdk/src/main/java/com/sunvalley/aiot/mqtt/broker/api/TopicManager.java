package com.sunvalley.aiot.mqtt.broker.api;

import java.util.List;

/**
 * manage topic
 * @author kai.li
 */
public interface TopicManager {

    List<MqttConnection> getConnectionsByTopic(String topic);

    void addTopicConnection(String topic, MqttConnection connection);

    void deleteTopicConnection(String topic, MqttConnection connection);

    List<String> getTopicByDeviceId(String deviceId);

    void putTopicSession(String deviceId, List<String> topics);
}
