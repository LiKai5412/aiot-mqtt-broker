package com.sunvalley.aiot.mqtt.broker.metric;

import com.google.common.collect.Maps;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author kai.li
 * @date 2020/4/27
 */

@RestControllerEndpoint(id = "mqttMetric")
public class MqttMetric {
    private static LongAdder totalConnectionCount = new LongAdder();
    private static LongAdder totalPublishCount = new LongAdder();
    private static LongAdder totalReceiveCount = new LongAdder();
    private static LongAdder totalPublishBytes = new LongAdder();
    private static LongAdder totalReceiveBytes = new LongAdder();
    private static ConcurrentHashMap<String, LongAdder> snMappingPublishBytesMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, LongAdder> snMappingReceiveBytesMap = new ConcurrentHashMap<>();

    @GetMapping("/publish/bytes/{sn}")
    public Map get(@PathVariable("sn") String sn) {
        Long publishBytes = snMappingPublishBytesMap.get(sn) != null ? snMappingPublishBytesMap.get(sn).longValue() : 0;
        Map<String, String> resultMap = Maps.newHashMap();
        resultMap.put(sn, convertUnit(publishBytes));
        return resultMap;
    }

    @GetMapping("/publish/count")
    public Long totalPublishCount() {
        return totalPublishCount.longValue();
    }

    @GetMapping("/publish/bytes")
    public String totalPublishBytes() {
        return convertUnit(totalPublishBytes.longValue());
    }

    @GetMapping("/receive/count")
    public Long totalReceiveCount() {
        return totalReceiveCount.longValue();
    }

    @GetMapping("/receive/bytes")
    public String totalReceiveBytes() {
        return convertUnit(totalReceiveBytes.longValue());
    }

    @GetMapping("/conn/count")
    public Long totalConnCount() {
        return totalConnectionCount.longValue();
    }

    public static void incrementTotalPublishCount() {
        totalPublishCount.increment();
    }

    public static void incrementTotalReceiveCount() {
        totalReceiveCount.increment();
    }

    public static void incrementTotalConnectionCount() {
        totalConnectionCount.increment();
    }

    public static void decrementTotalConnectionCount() {
        totalConnectionCount.decrement();
    }

    public static void addPublishBytes(long publishBytes) {
        totalPublishBytes.add(publishBytes);
    }

    public static void addReceiveBytes(long receiveBytes) {
        totalReceiveBytes.add(receiveBytes);
    }

    public static void removePublishBytesBySn(String sn){
        LongAdder snPublishBytesAdder = snMappingPublishBytesMap.get(sn);
        if (snPublishBytesAdder != null) {
            totalPublishBytes.add(-snPublishBytesAdder.longValue());
            snMappingPublishBytesMap.remove(sn);
        }
    }

    public static void addPublishBytesBySn(String sn, long publishBytes) {
        LongAdder snPublishBytesAdder = snMappingPublishBytesMap.get(sn);
        if (snPublishBytesAdder == null) {
            snPublishBytesAdder = new LongAdder();
            snMappingPublishBytesMap.put(sn, snPublishBytesAdder);
        }
        snPublishBytesAdder.add(publishBytes);
    }

    public static void addReceiveBytesBySn(String sn, long publishBytes) {
        LongAdder snReceiveBytesAdder = snMappingReceiveBytesMap.get(sn);
        if (snReceiveBytesAdder == null) {
            snReceiveBytesAdder = new LongAdder();
            snMappingReceiveBytesMap.put(sn, snReceiveBytesAdder);
        }
        snReceiveBytesAdder.add(publishBytes);
    }

    private String convertUnit(Long size) {
        if (size < 1024) {
            return String.valueOf(size + "B");
        } else if (size < 1024 * 1024) {
            return String.valueOf(size / 1024.0 + "K");
        } else if (size < 1024 * 1024 * 1024) {
            return String.valueOf(size / (1024 * 1024.0) + "M");
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            return String.valueOf(size / (1024 * 1024 * 1024.0) + "G");
        }
        return String.valueOf(size);
    }
}
