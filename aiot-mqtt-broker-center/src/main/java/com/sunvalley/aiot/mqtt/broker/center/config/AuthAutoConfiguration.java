package com.sunvalley.aiot.mqtt.broker.center.config;

import com.sunvalley.aiot.mqtt.broker.common.auth.IAuthService;
import com.sunvalley.aiot.token.client.facade.DeviceTokenServiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kai.li
 * @date 2020/1/13
 */
@Configuration
@Slf4j
public class AuthAutoConfiguration {

    @Autowired
    private DeviceTokenServiceFacade deviceTokenServiceFacade;

    @Value("${skip.authentication:false}")
    private boolean skipAuthentication;

    @Bean
    @ConditionalOnMissingBean
    public IAuthService authService(){
        return (productKey, password) ->{
            if(skipAuthentication){
                return true;
            }
            if(StringUtils.isBlank(productKey)){
                log.debug("ProductKey is blank");
                return false;
            }
            if(StringUtils.isBlank(password)){
                log.debug("Password is blank");
                return false;
            }
            return deviceTokenServiceFacade.checkDeviceToken(password);
        };
    }
}
