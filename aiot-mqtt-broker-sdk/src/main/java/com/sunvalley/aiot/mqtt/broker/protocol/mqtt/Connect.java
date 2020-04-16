/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.protocol.mqtt;

import com.sunvalley.aiot.mqtt.broker.api.ChannelManager;
import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.ClusterManager;
import com.sunvalley.aiot.mqtt.broker.common.auth.IAuthService;
import com.sunvalley.aiot.mqtt.broker.common.message.InternalMessage;
import com.sunvalley.aiot.mqtt.broker.utils.AttributeKeys;
import com.sunvalley.aiot.mqtt.broker.utils.MqttMessageBuilder;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.Attribute;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.Disposable;

import java.util.List;
import java.util.Optional;

/**
 * CONNECT连接处理
 *
 * @author kai.li
 */
@Slf4j
public class Connect {

    private IAuthService authService;

    private ChannelManager channelManager;

    private TopicManager topicManager;

    private ClusterManager clusterManager;

    public Connect(IAuthService authService, ChannelManager channelManager,
                   ClusterManager clusterManager, TopicManager topicManager) {
        this.authService = authService;
        this.channelManager = channelManager;
        this.topicManager = topicManager;
        this.clusterManager = clusterManager;
    }

    public boolean processConnect(MqttConnection connection, MqttConnectMessage msg) {
        // 消息解码器出现异常
        if (msg.decoderResult().isFailure()) {
            Throwable cause = msg.decoderResult().cause();
            if (cause instanceof MqttUnacceptableProtocolVersionException) {
                // 不支持的协议版本
                connection.sendConnAckMessage(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION, false).subscribe();
                return false;
            } else if (cause instanceof MqttIdentifierRejectedException) {
                // 不合格的deviceId
                connection.sendConnAckMessage(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false).subscribe();
                return false;
            }
            connection.dispose();
            return false;
        }
        // deviceId为空或null的情况, 这里要求客户端必须提供deviceId, 不管cleanSession是否为1, 此处没有参考标准协议实现
        if (StringUtils.isEmpty(msg.payload().clientIdentifier())) {
            connection.sendConnAckMessage(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, false).subscribe();
            return false;
        }
        // 用户名和密码验证, 这里要求客户端连接时必须提供用户名和密码, 不管是否设置用户名标志和密码标志为1, 此处没有参考标准协议实现
        String username = msg.payload().userName();
        String password = msg.payload().passwordInBytes() == null ? null : new String(msg.payload().passwordInBytes(), CharsetUtil.UTF_8);
        String deviceId = msg.payload().clientIdentifier();
        if (!authService.checkValid(deviceId, username, password)) {
            connection.sendConnAckMessage(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false).subscribe();
            return false;
        }
        Boolean sessionPresent = false;
        Boolean cleanSession = msg.variableHeader().isCleanSession();
        connection.setCleanSession(cleanSession);
        // 如果会话中已存储这个新连接的clientId, 就关闭之前该deviceId的连接
        if (channelManager.containsDeviceId(deviceId)) {
            MqttConnection mqttConnection = channelManager.getConnectionByDeviceId(deviceId);
            if (mqttConnection != null) {
                sessionPresent = true;
                mqttConnection.dispose();
            }
        }else {
            //发送集群消息,关闭其他节点上的相同deviceId的连接
            clusterManager.sendInternalMessage(InternalMessage.buildConnMessage(deviceId));
        }
        //获取保留的会话信息
        if (!cleanSession) {
            List<String> topics = topicManager.getTopicByDeviceId(deviceId);
            Optional.ofNullable(topics).ifPresent(tps -> {
                connection.setTopics(tps);
                tps.forEach(topic -> {
                    topicManager.addTopicConnection(topic, connection);
                });
            });
        }
        //设置遗嘱信息
        if (msg.variableHeader().isWillFlag()) {
            MqttPublishMessage willMessage = MqttMessageBuilder.buildPub(MqttQoS.valueOf(msg.variableHeader().willQos()),
                    msg.variableHeader().isWillRetain(), 0, msg.payload().willTopic(), msg.payload().willMessageInBytes());
            connection.getConnection().channel().attr(AttributeKeys.WILL_MESSAGE).set(willMessage);
        }
        // 存储会话信息及返回接受客户端连接
        channelManager.addConnections(connection);
        channelManager.addDeviceId(deviceId, connection);
        // 取消定时关闭连接
        Optional.ofNullable(connection.getConnection().channel().attr(AttributeKeys.CLOSE_CONNECTION))
                .map(Attribute::get)
                .ifPresent(Disposable::dispose);
        // 将deviceId存储到channel的map中
        connection.getConnection().channel().attr(AttributeKeys.DEVICE_ID).set(deviceId);
        //返回accept
        connection.sendConnAckMessage(MqttConnectReturnCode.CONNECTION_ACCEPTED, sessionPresent && !cleanSession).subscribe();
        log.debug("CONNECT - clientId: {}, cleanSession: {}", msg.payload().clientIdentifier(), msg.variableHeader().isCleanSession());
        return true;
    }

}
