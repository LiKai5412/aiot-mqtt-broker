package com.sunvalley.aiot.mqtt.broker.client.bean.kfk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.MessageType;
import com.sunvalley.otter.framework.core.utils.UtilJson;
import lombok.Builder;
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
@Builder
public class MqttPublishBo {
    @NotBlank
    private String topic;

    @NotBlank
    private String sn;

    @NotBlank
    private Object payload;

    private Date timestamp = new Date();

    private MessageType messageType;

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
