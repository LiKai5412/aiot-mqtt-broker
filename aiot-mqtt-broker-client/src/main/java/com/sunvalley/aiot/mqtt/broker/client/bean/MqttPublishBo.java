package com.sunvalley.aiot.mqtt.broker.client.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunvalley.otter.framework.core.utils.UtilJson;
import lombok.Data;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 12:01
 * @Desc: 下行消息
 */
@Data
public class MqttPublishBo {
    @NotBlank
    private String topic;

    @NotBlank
    private String clientId;

    @NotBlank
    private Object payload;

    private Date timestamp = new Date();

    @JsonIgnore
    public String getPayLoadStr() {
        Assert.notNull(payload, "value is null");
        if (payloadStr != null) {
            return payloadStr;
        }
        if (payload instanceof CharSequence) {
            payloadStr = payload.toString();
        } else {
            payloadStr = UtilJson.toString(payload);
        }
        return payloadStr;
    }

    @JsonIgnore
    private String payloadStr;
}
