package com.sunvalley.aiot.mqtt.broker.client.annotation;

import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Value("${mqtt.kafka.subscribe-topic}")
public @interface KafkaSubscribeTopic {
}
