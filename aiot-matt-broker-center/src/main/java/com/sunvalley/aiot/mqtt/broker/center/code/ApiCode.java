package com.sunvalley.aiot.mqtt.broker.center.code;

import com.sunvalley.otter.framework.core.code.IResultCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiCode implements IResultCode {

    /**
     * 数据添加失败
     */
    TEMPLATE_NOT_EXIST("01001", "模板不存在或者以过期"),
    TEMPLATE_ARGS_ERROR("01002", "参数值为空"),
    ;


    private String code;
    private String msg;
}
