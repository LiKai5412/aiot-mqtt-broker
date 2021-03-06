package com.sunvalley.aiot.mqtt.broker.config;

import io.netty.util.NettyRuntime;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author kai.li
 */
@Data
@ConfigurationProperties(prefix = "mqtt.tcp-server")
@Slf4j
public class MqttTcpServerProperties {

    private String ip;

    private int port;

    private String protocol;

    private int heartInSecond = 60;

    private int idleNumber = 1;

    private boolean printLog;

    private boolean tls;

    /**
     * 发送缓冲区大小
     * 默认 32k
     */
    private int sendBufSize = 32 * 1024;
    /**
     * 接收缓冲区大小
     * 默认 32k
     */
    private int revBufSize = 32 * 1024;

    /**
     * Socket参数，服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝。默认值
     * 这里默认设置 1024
     */
    private int backlog = 1024;

    /**
     * Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制，
     * 需要注意的是：默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
     */
    private boolean keepAlive = true;
    /**
     * Socket参数，立即发送数据，默认值为True（Netty默认为True而操作系统默认为False）。该值设置Nagle算法的启用，
     * 该算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，
     * 如果需要发送一些较小的报文，则需要禁用该算法。
     * Netty默认禁用该算法，从而最小化报文传输延时
     */
    private boolean noDelay = true;

    private int selectorNum = 1;

    private int workerNum = NettyRuntime.availableProcessors() * 2;

    private boolean reuseAddr = false;

    private int nodeId;

    /**
     * 证书下载地址
     */
    private String certUrl;

    /**
     * 证书秘钥下载地址
     */
    private String certPrivateKey;

    /**
     * 是否压力测试
     */
    private boolean pressure = false;

    private Consumer<Throwable> throwableConsumer = throwable -> {
        log.error(throwable.getMessage(), throwable);
    };


    public void checkConfig() {
        Objects.requireNonNull(ip, "ip is not null");
        Objects.requireNonNull(port, "protocol is not null");
    }
}
