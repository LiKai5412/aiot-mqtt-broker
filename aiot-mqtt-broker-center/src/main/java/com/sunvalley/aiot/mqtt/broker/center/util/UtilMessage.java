package com.sunvalley.aiot.mqtt.broker.center.util;

import com.sunvalley.aiot.mqtt.broker.client.bean.kfk.MqttMessageBo;
import com.sunvalley.aiot.mqtt.broker.client.enumeration.MessageType;
import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author kai.li
 * @date 2020/5/18
 */
public class UtilMessage {

    public static boolean isJson(byte[] messageBytes) {
        boolean result = false;
        if (messageBytes == null) {
            return result;
        }
        String msg = new String(messageBytes, StandardCharsets.UTF_8);
        if (StringUtils.isNotBlank(msg)) {
            msg = msg.trim();
            if (msg.startsWith("{") && msg.endsWith("}")) {
                result = true;
            } else if (msg.startsWith("[") && msg.endsWith("]")) {
                result = true;
            }
        }
        return result;
    }

    public static MessageType getMessageType(byte[] messageBytes) {
        return isJson(messageBytes) ? MessageType.JSON : MessageType.HEX;
    }
}
