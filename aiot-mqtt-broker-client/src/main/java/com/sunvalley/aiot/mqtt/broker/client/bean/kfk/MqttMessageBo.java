package com.sunvalley.aiot.mqtt.broker.client.bean.kfk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.MessageType;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.Method;
import com.sunvalley.otter.framework.core.utils.UtilDate;
import com.sunvalley.otter.framework.core.utils.UtilJson;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 12:01
 * @Desc: 下行消息
 */
@Data
@Builder
public class MqttMessageBo {
    @NotBlank
    private String productKey;

    @NotBlank
    private String sn;

    @NotBlank
    private Object payload;

    private long timestamp = UtilDate.toMilliseconds(LocalDateTime.now());

    private MessageType messageType;

    @JsonIgnore
    public String getPayLoadStr() {
        Assert.notNull(payload, "value is null");
        if (payload instanceof CharSequence) {
            payloadStr = payload.toString();
        } else {
            payloadStr = UtilJson.toString(payload);
        }
        return payloadStr;
    }

    @JsonIgnore
    @Getter
    private String payloadStr;

    public static void main(String[] args) {
        MqttJsonBo mqttJsonBo = MqttJsonBo.builder().state(Map.of("color", "red"))
                .command(Map.of("color", "blue")).method(Method.UPDATE.getValue())
                .metaData(MqttJsonBo.MetaData.builder().build().addCommandMetaData("color", "timestamp", 1234567890L)
                        .addStateMetaData("color", "timestamp", 5412L)).build();
        MqttMessageBo build = MqttMessageBo.builder().sn("5412").productKey("123456").payload(mqttJsonBo).timestamp(System.currentTimeMillis()).build();
        System.out.println(UtilJson.toJson(build));
    }
}
