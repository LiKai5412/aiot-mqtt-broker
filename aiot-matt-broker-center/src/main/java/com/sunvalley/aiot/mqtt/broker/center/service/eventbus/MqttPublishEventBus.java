package com.sunvalley.aiot.mqtt.broker.center.service.eventbus;

import com.sunvalley.aiot.mqtt.broker.center.bean.event.EventPublish;
import com.sunvalley.aiot.mqtt.broker.center.service.eventbus.codec.BaseMessageCodec;
import com.sunvalley.aiot.mqtt.broker.center.service.eventbus.codec.MessageCodecPublish;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 10:37
 * @Desc: 发送(回复)下行消息
 */
@Slf4j
@Service
@Validated
public class MqttPublishEventBus extends BaseEventBus<EventPublish> {

    @Value("${mqtt.event-address.publish:mqtt.event.publish}")
    private String address;

    /**
     * mqtt登录事件广播处理
     *
     * @param publish
     */
    @Override
    protected void handleMessage(EventPublish publish) {
        //  判断是否在本节点
        //  publish 消息
    }


    @Override
    protected String address() {
        return address;
    }

    @Override
    protected BaseMessageCodec<EventPublish> messageCodec() {
        return new MessageCodecPublish();
    }

}
