package com.sunvalley.aiot.mqtt.broker.center.service;

import com.sunvalley.aiot.mqtt.broker.transport.MqttTcpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import reactor.netty.DisposableServer;

/**
 * @author Kai.Li
 * @date 2020/3/13
 */
public class MqttServer implements ApplicationRunner {

    @Autowired
    private MqttTcpServer mqttTcpServer;

    @Override
    public void run(ApplicationArguments args){
        DisposableServer disposableServer = mqttTcpServer.start().block();
        disposableServer.onDispose().block();
    }
}
