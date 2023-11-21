package com.lz.rabbitmq.config;

import com.lz.rabbitmq.component.ConfirmCallBackService;
import com.lz.rabbitmq.component.ReturnCallBackService;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lizhao
 * @class BaseConfig
 * @description 基础配置类
 * @create 2023/11/16 16:39
 */
@Configuration
public class BaseConfig {

    private final ConfirmCallBackService confirmCallBackService;
    private final ReturnCallBackService returnCallBackService;

    public BaseConfig(ConfirmCallBackService confirmCallBackService, ReturnCallBackService returnCallBackService) {
        this.confirmCallBackService = confirmCallBackService;
        this.returnCallBackService = returnCallBackService;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        //设置交换机处理失败消息的模式：true表示消息由交换机到达不了队列时，会将消息重新返回给生产者，如果不设置这个指令，则交换机向队列推送消息失败后，不会触发setReturnCallback
        rabbitTemplate.setMandatory(true);
        //消息消费者确认收到消息后，手动ack回执
        rabbitTemplate.setConfirmCallback(confirmCallBackService);
        //消息由交换机投递到队列失败回调处理
        rabbitTemplate.setReturnsCallback(returnCallBackService);

        return rabbitTemplate;
    }
}
