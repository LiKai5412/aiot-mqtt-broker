package com.sunvalley.aiot.mqtt.broker.api;

import java.util.List;

public interface ChannelManager {
    List<MqttConnection> getConnections();


    void  addConnections(MqttConnection connection);


    void removeConnections(MqttConnection connection);

    void addDeviceId(String deviceId, MqttConnection connection);

    void removeDeviceId(String deviceId);

    MqttConnection getConnectionByDeviceId(String deviceId);

    Boolean containsDeviceId(String deviceId);
}
