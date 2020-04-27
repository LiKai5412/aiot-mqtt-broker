package com.sunvalley.aiot.mqtt.broker.client.bean.kfk;

import com.sunvalley.aiot.mqtt.broker.client.enumeration.Method;
import com.sunvalley.otter.framework.core.utils.UtilJson;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
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
    private State state;
    private MetaData metaData;
    private Long timestamp;

    @Getter
    @Builder
    public static class State {
        private Map<String, Object> reported;
        private Map<String, Object> desired;

        public State addReportedStatus(String statusName, Object value) {
            if (reported == null) {
                reported = new HashMap<>();
            }
            reported.put(statusName, value);
            return this;
        }
        public State addDesiredStatus(String statusName, Object value) {
            if (desired == null) {
                desired = new HashMap<>();
            }
            desired.put(statusName, value);
            return this;
        }
    }

    @Getter
    @Builder
    public static class MetaData {
        private Map<String, Map<String, Object>> reported;
        private Map<String, Map<String, Object>> desired;

        public MetaData addReportedMetaData(String statusName, String metaDataName, Object value) {
            if (reported == null) {
                reported = new HashMap<>();
            }
            Map<String, Object> metaDataMap = new HashMap<>();
            metaDataMap.put(metaDataName, value);
            reported.put(statusName, metaDataMap);
            return this;
        }

        public MetaData addDesiredMetaData(String statusName, String metaDataName, Object value) {
            if (desired == null) {
                desired = new HashMap<>();
            }
            Map<String, Object> metaDataMap = new HashMap<>();
            metaDataMap.put(metaDataName, value);
            desired.put(statusName, metaDataMap);
            return this;
        }
    }

   /* public static void main(String[] args) {
        State state = State.builder().build().addDesiredStatus("color", "red").addReportedStatus("color", "blue");
        MqttJsonBo mqttJsonBo = MqttJsonBo.builder().method(Method.UPDATE).state(state).
                timestamp(54123L).metaData(MetaData.builder().build().addDesiredMetaData("color", "timestamp", 1234567890L)
        .addReportedMetaData("color", "timestamp", 5412L)).build();
        System.out.println(UtilJson.toJson(mqttJsonBo));
    }*/
}
