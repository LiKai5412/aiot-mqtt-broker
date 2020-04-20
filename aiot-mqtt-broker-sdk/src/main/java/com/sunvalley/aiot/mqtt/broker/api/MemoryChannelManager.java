package com.sunvalley.aiot.mqtt.broker.api;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author kai.li
 * @date 2020/1/12
 */
public class MemoryChannelManager implements ChannelManager {
    private CopyOnWriteArrayList<MqttConnection> connections = new CopyOnWriteArrayList<>();

    private ConcurrentHashMap<String, MqttConnection> connectionMap = new ConcurrentHashMap<>();

    @Override
    public List<MqttConnection> getConnections() {
        return connections;
    }

    @Override
    public void addConnections(MqttConnection connection) {
        connections.add(connection);
    }

    @Override
    public void removeConnections(MqttConnection connection) {
        connections.remove(connection);
    }

    @Override
    public void addDeviceId(String deviceId, MqttConnection connection) {
        connectionMap.put(deviceId, connection);
    }

    @Override
    public void removeDeviceId(String deviceId) {
        connectionMap.remove(deviceId);
    }

    @Override
    public MqttConnection getConnectionByDeviceId(String deviceId) {
        return connectionMap.get(deviceId);
    }

    @Override
    public Boolean containsDeviceId(String deviceId) {
        return connectionMap.containsKey(deviceId);
    }
}
