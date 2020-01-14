package com.sunvalley.aiot.matt.broker.center;

import io.vertx.core.Vertx;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/10 17:21
 * @Desc: ****
 */
public class Vtest {
    public static void main(String[] args) {
        var vertx = Vertx.vertx();
        vertx.createHttpServer().requestHandler(req -> {

        }).listen(8080);
    }
}
