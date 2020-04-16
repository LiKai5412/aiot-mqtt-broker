package com.sunvalley.aiot.mqtt.broker.api;


import com.sunvalley.aiot.mqtt.broker.path.TopicMap;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author kai.li
 */
public class MemoryTopicManager implements TopicManager {

    LoadingCache<String, List<String>> topicSessionCache =
            CacheBuilder.newBuilder().expireAfterAccess(8, TimeUnit.HOURS)
                    .build(new CacheLoader<>() {
                        @Override
                        public List<String> load(String deviceId) {
                            return null;
                        }
                    });

    private TopicMap pathMap = new TopicMap();

    @Override
    public List<MqttConnection> getConnectionsByTopic(String topic) {
        String[] methodArray = topic.split("/");
        return Optional.ofNullable(pathMap.getData(methodArray)).orElse(Lists.newArrayList());
    }

    @Override
    public void addTopicConnection(String topic, MqttConnection connection) {
        String[] methodArray = topic.split("/");
        pathMap.putData(methodArray, connection);
    }

    @Override
    public void deleteTopicConnection(String topic, MqttConnection connection) {
        String[] methodArray = topic.split("/");
        pathMap.delete(methodArray, connection);
    }

    @Override
    public List<String> getTopicByDeviceId(String deviceId) {
        return topicSessionCache.getIfPresent(deviceId);
    }

    @Override
    public void putTopicSession(String deviceId, List<String> topics) {
        if(!CollectionUtils.isEmpty(topics)) {
            topicSessionCache.put(deviceId, topics);
        }
    }
}
