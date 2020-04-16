/**
 * Copyright (c) 2018, Mr.Wang (recallcode@aliyun.com) All rights reserved.
 */

package com.sunvalley.aiot.mqtt.broker.common.auth;

/**
 * 用户和密码认证服务接口
 *
 * @author kai.li
 */
public interface IAuthService {

    /**
     * 验证用户名和密码是否正确
     */
    boolean checkValid(String userName, String password);

    /**
     * @param deviceId
     * @param userName
     * @param password
     * @return
     */
    default boolean checkValid(String deviceId, String userName, String password) {
        return checkValid(userName, password);
    }

}
