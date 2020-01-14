package com.sunvalley.aiot.mqtt.broker.center.service.internal;

import com.sunvalley.otter.framework.core.utils.UtilJson;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: Simms.shi
 * @Date: 2020/1/13 15:00
 * @Desc: kfk 生产者处理类
 */
@Slf4j
@Service
@Validated
public class KafKaClientDelegate {

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    // async method
    public <T> void producerMessage(@NotBlank String topic, @NotNull T value) {
        log.debug("KafKa producer message start, topic:{},value:{}", topic, value);
        KafkaProducerRecord<String, String> record = KafkaProducerRecord.create(topic, UtilJson.toString(value));
        kafkaProducer.send(record, handlerResult);
    }


    private Handler<AsyncResult<RecordMetadata>> handlerResult = result -> {
        RecordMetadata metadata = result.result();
        if (log.isDebugEnabled()) {
            log.debug("KafKa producer message end,record metadata:{}", metadata.toJson());
        }
        if (result.failed()) {
            log.error("KafKa producer message,error:", result.cause());
        }
    };
}
