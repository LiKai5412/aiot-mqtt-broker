package com.sunvalley.aiot.mqtt.broker.client.domain.web;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author kai.li
 * @date 2020/6/15
 */
@Data
public class CommandBo {
    @NotNull
    private String sn;
    @NotNull
    private String productKey;
    @NotNull
    private JsonNode command;
}
