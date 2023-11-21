package com.lz.rabbitmq.component;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author lizhao
 * @class ReturnCallBackService
 * @description return回调方法（消息是否由交换机发送到队列中，失败时会回调该方法）
 * @create 2023/11/16 16:17
 */
@Component
@Slf4j
public class ReturnCallBackService implements RabbitTemplate.ReturnsCallback {

    @Override
    public void returnedMessage(ReturnedMessage returned) {
        log.info("ReturnCallBackService --> message send failed, errorMsg: {}", JSON.toJSONString(returned));
    }
}
