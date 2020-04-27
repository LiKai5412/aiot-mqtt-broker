package com.sunvalley.aiot.mqtt.broker.client.enumeration;

public enum Method {
    GET("get"), CONTROL("control"), DELETE("delete"), UPDATE("update"), REPLY("reply");
    private String name;
    Method(String name){
        this.name = name;
    }
}
