package com.sunvalley.aiot.mqtt.broker.center.service.eventbus;

import com.sunvalley.aiot.mqtt.broker.center.bean.event.EventLogin;
import com.sunvalley.aiot.mqtt.broker.center.service.eventbus.codec.BaseMessageCodec;
import com.sunvalley.aiot.mqtt.broker.center.service.eventbus.codec.MessageCodecLogin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 10:37
 * @Desc: 登录事件
 */
@Slf4j
@Service
@Validated
public class MqttLoginBroker extends EventBusBroker<EventLogin> {

    @Value("${mqtt.event-address.login:mqtt.event.login}")
    private String address;

    /**
     * mqtt登录事件广播处理
     *
     * @param event
     */
    @Override
    protected void handleMessage(EventLogin event) {
        log.debug("handleMessage.eventLogin:{}", event);
        // 下线掉 本节点已上线设备
//        worker.executeBlocking();
    }


    @Override
    protected String address() {
        return address;
    }

    @Override
    protected BaseMessageCodec<EventLogin> messageCodec() {
        return new MessageCodecLogin();
    }
}
