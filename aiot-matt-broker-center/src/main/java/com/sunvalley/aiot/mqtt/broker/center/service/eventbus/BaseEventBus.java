package com.sunvalley.aiot.mqtt.broker.center.service.eventbus;

import com.sunvalley.aiot.mqtt.broker.center.service.eventbus.codec.BaseMessageCodec;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/14 9:17
 * @Desc: eventBus抽象类
 */
public abstract class BaseEventBus<E> implements InitializingBean {

    private Logger log0 = LoggerFactory.getLogger(getClass());

    @Autowired
    protected Vertx vertx;


    private Class<E> clazz = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];

    @Override
    public void afterPropertiesSet() throws Exception {
        vertx.eventBus().registerDefaultCodec(clazz, messageCodec());
        MessageConsumer<E> consumer = vertx.eventBus().consumer(address());
        consumer.handler(event -> handleMessage(event.body()));
        consumer.exceptionHandler(this::handleException);
    }

    /**
     * mqtt登录事件广播
     *
     * @param event
     */
    public void broadMessage(E event) {
        log0.debug("broadMessage event:{}", event);
        vertx.eventBus().publish(address(), event);
    }

    /**
     * mqtt登录事件广播处理
     *
     * @param event
     */
    protected abstract void handleMessage(E event);

    /**
     * mqtt登录事件广播(异常)处理
     *
     * @param throwable
     */
    protected void handleException(Throwable throwable) {
        log0.error("EventBusBroker.handleException:", throwable);
    }

    /**
     * eventBus 接收和发送地址
     *
     * @return
     */
    protected abstract String address();

    /**
     * 自定义编解码器
     *
     * @return
     */
    protected abstract BaseMessageCodec<E> messageCodec();
}