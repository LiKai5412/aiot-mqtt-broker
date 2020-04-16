package com.sunvalley.aiot.mqtt.broker.api;

import com.sunvalley.aiot.mqtt.broker.common.message.RetainMessage;
import com.sunvalley.aiot.mqtt.broker.common.message.TransportMessage;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author kai.li
 * @date 2020/1/14
 */
public class MemoryMessageManager implements MessageManager {

    private LoadingCache<String, RetainMessage> retainMessageCache =
            CacheBuilder.newBuilder().expireAfterAccess(8, TimeUnit.HOURS)
                    .build(new CacheLoader<>() {
                        @Override
                        public RetainMessage load(String deviceId) {
                            return null;
                        }
                    });

    private LoadingCache<String, TransportMessage> qos2MessageCache =
            CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.HOURS)
                    .build(new CacheLoader<>() {
                        @Override
                        public TransportMessage load(String deviceMsgId) {
                            return null;
                        }
                    });

    @Override
    public void saveRetainMessage(String topicName, boolean dup, int qos, byte[] copyByteBuf) {
        RetainMessage retainMessage = new RetainMessage(dup, qos, topicName, copyByteBuf);
        retainMessageCache.put(topicName, retainMessage);
    }

    @Override
    public Optional<RetainMessage> getRetainMessage(String topicName) {
        return Optional.ofNullable(retainMessageCache.getIfPresent(topicName));
    }

    @Override
    public void removeRetainMessage(String topicName) {
        retainMessageCache.invalidate(topicName);
    }
}
