package com.sunvalley.aiot.mqtt.broker.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WillMessage {
    private int qos;
    private String topicName;
    private byte[] copyByteBuf;
    private boolean retain;
}
