package com.sunvalley.aiot.mqtt.broker.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/10 13:53
 * @Desc: ****
 */
@SpringBootApplication
@EnableDiscoveryClient
//@EnableFeignClients(clients = {MessageProviderFacade.class})
public class MqttBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttBrokerApplication.class, args);
    }
}
