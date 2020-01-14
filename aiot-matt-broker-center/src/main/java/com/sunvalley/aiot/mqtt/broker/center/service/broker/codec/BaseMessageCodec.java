package com.sunvalley.aiot.mqtt.broker.center.service.broker.codec;

import com.sunvalley.otter.framework.core.utils.UtilJson;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import lombok.Getter;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 9:51
 * @Desc: event bus 自定义编解码器
 * 单继承该基类
 */
public abstract class BaseMessageCodec<S> implements MessageCodec<S, S> {


    @Override
    public void encodeToWire(Buffer buffer, S s) {
        Assert.notNull(s, "encodeToWire.object is null");
        buffer.appendBytes(UtilJson.writeValueAsBytes(s));
    }

    @Override
    public S decodeFromWire(int pos, Buffer buffer) {
        return UtilJson.readValue(buffer.getBytes(), clazz);
    }

    @Override
    public S transform(S s) {
        return s;
    }

    @Override
    public String name() {
        return clazz.getName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }


    private Class<S> clazz = (Class<S>) ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];

}
