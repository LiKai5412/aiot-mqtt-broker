package com.sunvalley.aiot.mqtt.broker.common.message;

import lombok.Builder;
import lombok.Getter;

/**
 * Qos2消息
 *
 * @author kai.li
 */
@Getter
@Builder
public class TransportMessage {

    private String topic;

    private byte[] message;

    private int qos;

    private boolean isRetain;

    private boolean isDup;

}
