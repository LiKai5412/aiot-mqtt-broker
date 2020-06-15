package com.sunvalley.aiot.mqtt.broker.center.controller;


import com.sunvalley.aiot.mqtt.broker.center.util.MqttMessageUtils;
import com.sunvalley.aiot.mqtt.broker.client.domain.web.CommandBo;
import lombok.SneakyThrows;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Kai.Li
 * @date 2020/3/13
 */
@RestController
@RequestMapping("/broker")
@Validated
public class MqttWebClientController {

    @SneakyThrows
    @PostMapping(value = "api")
    public void control(@Valid @RequestBody CommandBo commandBo) {
        MqttMessageUtils.sendPublishMessage(commandBo);
    }
}
