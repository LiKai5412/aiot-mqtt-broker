package com.sunvalley.aiot.mqtt.broker.center.config;

import com.sunvalley.otter.framework.base.context.async.OtterAsyncProperties;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.*;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.mqtt.MqttTopicSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/10 14:33
 * @Desc: mqtt broker
 */
@Configuration
@EnableConfigurationProperties(MqttBrokerProperties.class)
public class MqttBrokerConfiguration {

    @Autowired
    private MqttBrokerProperties mqttBrokerProperties;

    @Bean
    public Vertx vertx() {
        VertxOptions options = new VertxOptions();
        Vertx vertx = Vertx.vertx(options);
        return vertx;
    }


    @Bean
    public Handler<MqttEndpoint> handler() {
        return endpoint -> {
            // shows main connect info
            System.out.println("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());
            if (endpoint.auth() != null) {
                System.out.println("[username = " + endpoint.auth().getUsername() + ", password = " + endpoint.auth().getPassword() + "]");
            }
            if (endpoint.will() != null && endpoint.will().isWillFlag()) {
                System.out.println("[will topic = " + endpoint.will().getWillTopic() +
                        " msg = " + new String(endpoint.will().getWillMessageBytes()) +
                        " QoS = " + endpoint.will().getWillQos() + " isRetain = " + endpoint.will().isWillRetain() + "]");
            }
            System.out.println("[keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");


            endpoint.subscribeHandler(subscribe -> {

                List<MqttQoS> grantedQosLevels = new ArrayList<>();
                for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
                    System.out.println("Subscription for " + s.topicName() + " with QoS " + s.qualityOfService());
                    grantedQosLevels.add(s.qualityOfService());
                }
                // ack the subscriptions request
                endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);

            });

            // handling requests for unsubscriptions
            endpoint.unsubscribeHandler(unsubscribe -> {
                for (String t : unsubscribe.topics()) {
                    System.out.println("Unsubscription for " + t);
                }
                // ack the subscriptions request
                endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
            });

            // handling incoming published messages
            endpoint.publishHandler(message -> {
                System.out.println("Just received message [" + message.payload().toString(Charset.defaultCharset()) + "] with QoS [" + message.qosLevel() + "]");
                if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                    endpoint.publishAcknowledge(message.messageId());
                } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                    endpoint.publishReceived(message.messageId());
                }
            }).publishReleaseHandler(messageId -> {
                endpoint.publishComplete(messageId);
            });

            // handling ping from client
            endpoint.pingHandler(v -> {
                System.out.println("Ping received from client");
            });


            // accept connection from the remote client
            endpoint.accept(false);

        };
    }

    @Bean
    public Handler<AsyncResult<MqttServer>> listenHandler() {
        return ar -> {
            if (ar.succeeded()) {
                System.out.println("MQTT server is listening on port " + ar.result().actualPort());
            } else {
                System.out.println("Error on starting the server");
                ar.cause().printStackTrace();
            }
        };
    }

    @Bean
    public List<MqttServer> mqttServers(Vertx vertx,
                                        Handler<MqttEndpoint> handler,
                                        Handler<AsyncResult<MqttServer>> listenHandler) {

        MqttServerOptions options = new MqttServerOptions();
        List<MqttServer> servers = new ArrayList<>();

        for (int i = 0; i < mqttBrokerProperties.getMaxProcess(); i++) {
            options.setPort(mqttBrokerProperties.getServerPort());
            MqttServer mqttServer = MqttServer.create(vertx, options);
            mqttServer.endpointHandler(handler).listen(listenHandler);
            servers.add(mqttServer);
        }

        return servers;
    }


}
