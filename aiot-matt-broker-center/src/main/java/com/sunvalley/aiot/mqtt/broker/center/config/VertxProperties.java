package com.sunvalley.aiot.mqtt.broker.center.config;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.eventbus.EventBus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 9:40
 * @Desc: ****
 */
@Configuration
@ConfigurationProperties("vertx")
@Getter
@Setter
public class VertxProperties {


    private String zookeeperHosts;

    private Integer workerThreads = 128;
    /**
     * 异步核心线程数，默认：2
     */
    private Kafka kafka;


    @Getter
    @Setter
    public static class Kafka {
        private String bootstrapServer;
    }

}
