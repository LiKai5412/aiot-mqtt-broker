package com.sunvalley.aiot.mqtt.broker.client.bean.kfk;

import com.sunvalley.aiot.mqtt.broker.client.enumeration.Method;
import com.sunvalley.otter.framework.core.utils.UtilJson;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kai.li
 * @date 2020/4/27
 */

@Data
@Builder
public class MqttJsonBo {
    private Method method;
    private Map<String, Object> state;
    private Map<String, Object> command;
    private MetaData metaData;
    private Long timestamp;

    @Getter
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
