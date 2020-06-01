package com.sunvalley.aiot.mqtt.broker.client.enumeration;

import java.util.Arrays;

/**
 * @author kai.li
 * @date 2020/4/27
 * 在线状态枚举
 */
public enum LinkStatus {
    /**
     * 在线
     */
    X_ONLINE(0),

    /**
     * 离线
     */
    X_OFFLINE(1);

    LinkStatus(int status) {
        this.online = status;
    }

    public int value() {
        return online;
    }

    public static LinkStatus parse(int status) {
        return Arrays.stream(LinkStatus.values()).filter(linkStatus -> linkStatus.value() == status).findFirst().orElse(null);
    }

    private int online;

}
