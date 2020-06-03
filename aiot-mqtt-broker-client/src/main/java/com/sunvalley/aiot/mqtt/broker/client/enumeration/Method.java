package com.sunvalley.aiot.mqtt.broker.client.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Method {
    GET("get"), CONTROL("control"), DELETE("delete"), UPDATE("update"), REPLY("reply");

    private String name;
    Method(String name){
        this.name = name;
    }

    /*@JsonValue
    public String getValue() {
        return name.toLowerCase();
    }*/

    @JsonCreator
    public static Method get(String name){
        return Method.valueOf(name.toUpperCase());
    }
}
