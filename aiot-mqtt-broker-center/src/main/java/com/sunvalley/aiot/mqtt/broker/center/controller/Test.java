package com.sunvalley.aiot.mqtt.broker.center.controller;

import com.sunvalley.aiot.mqtt.broker.client.service.BrokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kai.li
 * @date 2020/5/25
 */
@RestController("/test")
public class Test {
    @Autowired
    private BrokerService brokerService;

    @GetMapping("/brokerServiceTest")
    public void brokerServiceTest(){
        brokerService.disconnDeviceBySn("1ebeb5cbc1664ccd9d9dfe0afb506e3b");
    }
}
