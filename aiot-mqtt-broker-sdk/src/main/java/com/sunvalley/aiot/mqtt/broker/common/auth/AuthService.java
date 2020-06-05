/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.common.auth;

import com.sunvalley.aiot.mqtt.broker.api.MqttConnection;
import org.springframework.util.StringUtils;

/**
 * 用户名和密码认证服务
 */
public class AuthService implements IAuthService {

    @Override
    public boolean checkValid(String deviceId, String userName, String password) {
        if (StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(userName)  || StringUtils.isEmpty(password)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkValid(String userName, String password) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkValid(MqttConnection connection, String userName, String password) {
        return false;
    }
}
