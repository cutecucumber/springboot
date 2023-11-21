package com.lz.rabbitmq.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author lizhao
 * @class ConfirmCallBackService
 * @description confirm回调方法（消息是否发送到交换机中，成功与否都会回调该方法）
 * @create 2023/11/16 16:17
 */
@Component
@Slf4j
public class ConfirmCallBackService implements RabbitTemplate.ConfirmCallback {

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("ConfirmCallBackService --> message send succeed.");
        } else {
            log.error("ConfirmCallBackService --> message send failed, cause: {}", cause);
        }
    }
}
