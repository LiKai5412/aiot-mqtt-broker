package com.sunvalley.aiot.mqtt.broker.client.enumeration;

/**
 * @author kai.li
 */

public enum MessageType {
    /**
     * json格式
     */
    JSON("json"),
    /**
     * 16进制格式
     */
    HEX("hex");

    MessageType(String name) {
        this.name = name;
    }

    private String name;
}
