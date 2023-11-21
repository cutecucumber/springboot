package com.lz.rabbitmq.controller;

import com.alibaba.fastjson.JSON;
import com.lz.rabbitmq.service.sender.SenderMessageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.lz.rabbitmq.config.QueueConfig.BUSINESS_EXCHANGE_NAME;
import static com.lz.rabbitmq.config.QueueConfig.BUSINESS_ROUTING_KEY;

/**
 * @author lizhao
 * @class MessageController
 * @description 消息发送类
 * @create 2023/11/17 11:32
 */
@RestController
@RequestMapping
public class MessageController {

    private final SenderMessageService senderMessageService;

    public MessageController(SenderMessageService senderMessageService) {
        this.senderMessageService = senderMessageService;
    }

    @PostMapping(value = "/send")
    public void sendMessage(@RequestBody Map<String, String> map) {
        senderMessageService.sendMessage(BUSINESS_EXCHANGE_NAME, BUSINESS_ROUTING_KEY, JSON.toJSONString(map));
    }
}
