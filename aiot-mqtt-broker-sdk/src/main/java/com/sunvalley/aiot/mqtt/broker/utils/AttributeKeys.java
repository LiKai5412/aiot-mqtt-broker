package com.sunvalley.aiot.mqtt.broker.utils;

import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.util.AttributeKey;
import reactor.core.Disposable;

/**
 * @author kai.li
 */
public interface AttributeKeys {

    AttributeKey<Disposable> CLOSE_CONNECTION = AttributeKey.valueOf("close_connection");

    AttributeKey<String> DEVICE_ID = AttributeKey.valueOf("device_id");

    AttributeKey<String> PRODUCT_KEY = AttributeKey.valueOf("product_key");

    AttributeKey<MqttPublishMessage> WILL_MESSAGE = AttributeKey.valueOf("WILL_MESSAGE");

    AttributeKey<String> V_SN = AttributeKey.valueOf("vsn");

}
