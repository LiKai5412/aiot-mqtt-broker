package com.sunvalley.aiot.mqtt.broker.client.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Method {
    GET("get", 0), CONTROL("control", 1),
    DELETE("delete", 2), UPDATE("update", 3), REPLY("reply", 4);

    private String name;
    private int code;

    Method(String name, int code) {
        this.name = name;
        this.code = code;
    }

    @JsonValue
    public int getValue() {
        return code;
    }

}
