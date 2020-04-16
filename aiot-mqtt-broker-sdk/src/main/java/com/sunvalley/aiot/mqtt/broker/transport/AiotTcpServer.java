package com.sunvalley.aiot.mqtt.broker.transport;

import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;

public interface AiotTcpServer {
    Mono<? extends DisposableServer> start();
}
