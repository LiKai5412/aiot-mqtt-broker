package com.sunvalley.aiot.mqtt.broker.config;

import com.sunvalley.aiot.mqtt.broker.common.auth.AuthService;
import com.sunvalley.aiot.mqtt.broker.common.auth.IAuthService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kai.li
 * @date 2020/1/13
 */
@Configuration
public class AuthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IAuthService authService(){
        return new AuthService();
    }
}
