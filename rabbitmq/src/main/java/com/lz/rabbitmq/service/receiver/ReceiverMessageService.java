package com.lz.rabbitmq.service.receiver;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.lz.rabbitmq.config.QueueConfig.BUSINESS_QUEUE_NAME;
import static com.lz.rabbitmq.config.QueueConfig.Dlx_QUEUE_NAME;

/**
 * @author lizhao
 * @class ReceiverMessageService
 * @description 消息监听类
 * @create 2023/11/17 8:58
 */
@Component
@Slf4j
public class ReceiverMessageService {

    @RabbitListener(queues = {BUSINESS_QUEUE_NAME})
    @RabbitHandler
    public void consumeMessage(Channel channel, Message message, String args) {
        try {
            log.info("ReceiverMessageService#consumeMessage --> accept message successfully, message: {}", args);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("ReceiverMessageService#consumeMessage --> consume message failed, errorMsg: {}", e.getMessage());
            if (message.getMessageProperties().getRedelivered()) {
                try {
                    log.error("ReceiverMessageService#consumeMessage --> message has consumed repeated, reject accept again");
                    //拒绝消息，requeue=false，表示不再重新入队，如果配置了死信队列则进入死信队列
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                try {
                    log.error("ReceiverMessageService#consumeMessage --> message return queue again");
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    @RabbitListener(queues = {Dlx_QUEUE_NAME})
    @RabbitHandler
    public void dlxConsumeMessage(Channel channel, Message message, String args) {
        log.info("ReceiverMessageService#dlxConsumeMessage --> accept dlx message successfully, message: {}", args);
        //入库记录
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
