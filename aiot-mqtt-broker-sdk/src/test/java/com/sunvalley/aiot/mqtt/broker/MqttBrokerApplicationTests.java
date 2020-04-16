package com.sunvalley.aiot.mqtt.broker;

import com.sunvalley.aiot.mqtt.broker.transport.MqttTcpServer;
import com.sunvalley.aiot.mqtt.broker.transport.MqttTcpServerBuilder;
import reactor.netty.DisposableServer;

class MqttBrokerApplicationTests {

    public static void main(String[] args) {
        MqttTcpServer mqttTcpServer = new MqttTcpServerBuilder("localhost", 8084).heartInSecond(11)
                .log(true).keepAlive(true).noDelay(true).tls(true).build();
        DisposableServer disposableServer = mqttTcpServer.start().block();
        disposableServer.onDispose().block();
    }
}
