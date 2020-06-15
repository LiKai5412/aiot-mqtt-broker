package com.sunvalley.aiot.mqtt.broker.client.domain.kfk;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kai.li
 * @date 2020/4/27
 */

@Data
@Builder
public class MqttJsonBo {
    private int method;
    private Map<String, Object> state;
    private Map<String, Object> command;
    private MetaData metaData;

    @Data
    @Builder
    public static class MetaData {
        private Map<String, Map<String, Object>> state;
        private Map<String, Map<String, Object>> command;

        public MetaData addStateMetaData(String statusName, String metaDataName, Object value) {
            if (state == null) {
                state = new HashMap<>();
            }
            addMetaData(state, statusName, metaDataName, value);
            return this;
        }

        public MetaData addCommandMetaData(String statusName, String metaDataName, Object value) {
            if (command == null) {
                command = new HashMap<>();
            }
            addMetaData(command, statusName, metaDataName, value);
            return this;
        }

        private void addMetaData(Map<String, Map<String, Object>> map, String statusName, String metaDataName, Object value) {
            Map<String, Object> metaDataMap = new HashMap<>();
            metaDataMap.put(metaDataName, value);
            map.put(statusName, metaDataMap);
        }
    }
}
