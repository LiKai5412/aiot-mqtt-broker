package com.sunvalley.aiot.mqtt.broker.protocol;

import com.sunvalley.aiot.mqtt.broker.api.ChannelManager;
import com.sunvalley.aiot.mqtt.broker.api.MessageManager;
import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import com.sunvalley.aiot.mqtt.broker.api.TopicManager;
import com.sunvalley.aiot.mqtt.broker.api.cluster.ClusterManager;
import com.sunvalley.aiot.mqtt.broker.common.auth.IAuthService;
import com.sunvalley.aiot.mqtt.broker.protocol.mqtt.*;
import io.netty.handler.codec.mqtt.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author kai.li
 * @date 2020/1/13
 */
@Data
@Slf4j
public class ProtocolProcessor {
    private Connect connect;

    private DisConnect disConnect;

    private PingResp pingResp;

    private Publish publish;

    private PubAck pubAck;

    private PubRec pubRec;

    private PubRel pubRel;

    private PubComp pubComp;

    private Subscribe subscribe;

    private UnSubscribe unSubscribe;

    private IAuthService authService;

    private ChannelManager channelManager;

    private MessageManager messageManager;

    private TopicManager topicManager;

    public ProtocolProcessor(){

    }

    public ProtocolProcessor(IAuthService authService, ChannelManager channelManager,
                             MessageManager messageManager, TopicManager topicManager, ClusterManager clusterManager) {
        this.authService = authService;
        this.channelManager = channelManager;
        this.topicManager = topicManager;
        this.connect = new Connect(authService, channelManager, clusterManager, topicManager);
        this.pingResp = new PingResp();
        this.disConnect = new DisConnect();
        this.publish = new Publish(messageManager, topicManager, clusterManager);
        this.pubAck = new PubAck();
        this.pubRec = new PubRec();
        this.pubRel = new PubRel(topicManager, clusterManager);
        this.pubComp = new PubComp();
        this.subscribe = new Subscribe(topicManager, messageManager);
        this.unSubscribe = new UnSubscribe(topicManager);
    }

    public void process(MqttConnection connection, MqttMessage message) {
        log.info("accept message connection {} info{}", connection.getConnection(), message);
        switch (message.fixedHeader().messageType()) {
            case CONNECT:
                connect.processConnect(connection, (MqttConnectMessage) message);
                break;
            case PUBLISH:
                publish.processPublish(connection, (MqttPublishMessage) message);
                break;
            case PUBACK:
                pubAck.processPubAck(connection, (MqttPubAckMessage) message);
                break;
            case PUBREC:
                pubRec.processPubRec(connection, message);
                break;
            case PUBREL:
                pubRel.processPubRel(connection, message);
                break;
            case PUBCOMP:
                pubComp.processPubComp(connection, message);
                break;
            case SUBSCRIBE:
                subscribe.processSubscribe(connection, (MqttSubscribeMessage) message);
                break;
            case UNSUBSCRIBE:
                unSubscribe.processUnSubscribe(connection, (MqttUnsubscribeMessage) message);
                break;
            case PINGREQ:
                pingResp.processPingResp(connection);
                break;
            case DISCONNECT:
                disConnect.processDisConnect(connection);
                break;
            default:
                break;
        }
    }
}
