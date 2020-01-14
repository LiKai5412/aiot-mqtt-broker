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
 * @Desc: 上行(原始)消息
 */
@Data
public class MqttBeatBo {


    private Date timestamp = new Date();

    @NotBlank
    private String clientId;


}
