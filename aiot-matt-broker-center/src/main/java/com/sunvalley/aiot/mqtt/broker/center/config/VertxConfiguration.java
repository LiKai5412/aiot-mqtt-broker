package com.sunvalley.aiot.mqtt.broker.center.config;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 9:40
 * @Desc: Vertx组件Configuration
 */
@Configuration
@EnableConfigurationProperties(VertxProperties.class)
public class VertxConfiguration {

    @Autowired
    private VertxProperties vertxProperties;

    @Bean
    public Vertx vertx() {
        VertxOptions options = new VertxOptions();
        /*
        【非常重要】 work线程数量,默认128
          在业务处理中,提升并发度
          在io处理尽量执行(非阻塞)异步代码,
          同时避免锁竞争
         */
        options.setWorkerPoolSize(vertxProperties.getWorkerThreads());

        JsonObject zkConfig = new JsonObject();
        zkConfig.put("zookeeperHosts", vertxProperties.getZookeeperHosts());
        zkConfig.put("rootPath", "io.vertx");
        zkConfig.put("retry", new JsonObject()
                .put("initialSleepTime", 3000)
                .put("maxTimes", 3));
        ClusterManager mgr = new ZookeeperClusterManager(zkConfig);
        options.setClusterManager(mgr);

        Vertx vertx = Vertx.vertx(options);
        return vertx;
    }


    @Bean
    public KafkaProducer<String, String> kafkaProducer(Vertx vertx) {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", vertxProperties.getKafka().getBootstrapServer());
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("acks", "1");

        // use producer for interacting with Apache Kafka
        KafkaProducer<String, String> producer = KafkaProducer.create(vertx, config);
        return producer;
    }

}
