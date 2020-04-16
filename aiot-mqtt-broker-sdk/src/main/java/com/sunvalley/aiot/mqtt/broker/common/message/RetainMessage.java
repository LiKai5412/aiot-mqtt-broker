package com.sunvalley.aiot.mqtt.broker.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RetainMessage
{
    private boolean dup;
    private int qos;
    private String topicName;
    private byte[] copyByteBuf;
}
