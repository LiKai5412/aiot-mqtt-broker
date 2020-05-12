package com.sunvalley.aiot.mqtt.broker.center;

import com.sunvalley.aiot.token.client.facade.DeviceTokenServiceFacade;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/10 13:53
 * @Desc: 启动入口
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = DeviceTokenServiceFacade.class)
public class  MqttBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttBrokerApplication.class, args);
    }
}
