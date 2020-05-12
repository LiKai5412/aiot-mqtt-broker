package com.sunvalley.aiot.mqtt.broker.transport;

import com.sunvalley.aiot.mqtt.broker.api.*;
import com.sunvalley.aiot.mqtt.broker.config.MqttTcpServerProperties;
import com.google.common.collect.Lists;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;
import reactor.netty.DisposableServer;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpServer;

import java.util.List;
import java.util.Objects;

/**
 * @author kai.li
 */
@Slf4j
public class MqttTcpServer implements AiotTcpServer {

    private MqttTcpServerProperties mqttTcpServerProperties;
    private UnicastProcessor<MqttConnection> unicastProcessor;
    private TopicManager topicManager;
    private ChannelManager channelManager;
    private MessageManager messageManager;
    private FluxSink<MqttConnection> fluxSink;

    public MqttTcpServer(MqttTcpServerProperties mqttTcpServerProperties,
                         TopicManager topicManager, ChannelManager channelManager, ConnectionSubscriber connectionSubscriber, MessageManager messageManager) {
        this.mqttTcpServerProperties = mqttTcpServerProperties;
        this.topicManager = topicManager;
        this.channelManager = channelManager;
        unicastProcessor = UnicastProcessor.create();
        fluxSink = unicastProcessor.sink();
        unicastProcessor.subscribe(connectionSubscriber);
        this.messageManager = messageManager;
    }


    @Override
    public Mono<? extends DisposableServer> start() {
        mqttTcpServerProperties.checkConfig();
        return buildServer()
                .doOnConnection(connection -> {
                    getHandlers().forEach(connection::addHandlerLast);
                    fluxSink.next(new MqttConnection(connection, topicManager, channelManager, messageManager));
                })
                .bind().doOnError(mqttTcpServerProperties.getThrowableConsumer());
    }

    private TcpServer buildServer() {
        LoopResources loop = LoopResources.create("tcp-server-loop", mqttTcpServerProperties.getSelectorNum(),
                mqttTcpServerProperties.getWorkerNum(), true);
        TcpServer server = TcpServer.create()
                .port(mqttTcpServerProperties.getPort())
                .wiretap(mqttTcpServerProperties.isLog())
                .host(mqttTcpServerProperties.getIp())
                .runOn(loop)
                .selectorOption(ChannelOption.SO_BACKLOG, mqttTcpServerProperties.getBacklog())
                .option(ChannelOption.TCP_NODELAY, mqttTcpServerProperties.isNoDelay())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_KEEPALIVE, mqttTcpServerProperties.isKeepAlive())
                .option(ChannelOption.SO_RCVBUF, mqttTcpServerProperties.getRevBufSize())
                .option(ChannelOption.SO_SNDBUF, mqttTcpServerProperties.getSendBufSize())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
        return mqttTcpServerProperties.isTls() ? server.secure(sslContextSpec -> sslContextSpec.sslContext(Objects.requireNonNull(buildContext()))) : server;
    }

    private SslContext buildContext() {
        try {
            ClassPathResource serverCrt = new ClassPathResource("server.crt");
            ClassPathResource pkcs8 = new ClassPathResource("pkcs8_server.key");
            return SslContextBuilder.forServer(serverCrt.getInputStream(), pkcs8.getInputStream()).build();
        } catch (Exception e) {
            log.error("*******************************************************************ssl error: {}", e.getMessage());
        }
        return null;
    }

    private List<ChannelHandler> getHandlers() {
        return Lists.newArrayList(new MqttDecoder(5 * 1024 * 1024), MqttEncoder.INSTANCE);
    }
}
