package com.sunvalley.aiot.mqtt.broker.client.bean.kfk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunvalley.otter.framework.core.utils.UtilJson;
import lombok.Data;
import org.springframework.util.Assert;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 12:01
 * @Desc: 上行(登录)消息
 */
@Data
public class MqttLoginBo {


    private Date timestamp = new Date();

    @NotBlank
    private String type;

    @NotBlank
    private String payload;

    @JsonIgnore
    public <T> void setPayLoadValue(T t) {
        Assert.notNull(t, "payLoad value is null");
        if (t instanceof CharSequence) {
            payload = t.toString();
        } else {
            payload = UtilJson.toString(t);
        }
    }
}
