package com.lz.rabbitmq.service.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author lizhao
 * @class SenderMessageService
 * @description 消息发送类
 * @create 2023/11/17 8:54
 */
@Service
@Slf4j
public class SenderMessageService {

    private final RabbitTemplate rabbitTemplate;

    public SenderMessageService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String exchange, String routingKey, String args) {
        String id = UUID.randomUUID().toString();
        rabbitTemplate.convertAndSend(exchange, routingKey, args,
                message -> {
                    //消息持久化
                    MessageProperties messageProperties = message.getMessageProperties();
                    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    messageProperties.setMessageId(id);
                    //消息过期时间已在队列中进行了声明
                    return message;
                },
                //该对象作为confirm回调方法的参数（@com.lz.rabbitmq.component.ConfirmCallBackService#confirm()）
                new CorrelationData(id));
    }
}
