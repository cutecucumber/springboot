package com.lz.realtimepush.websocket.controller;

import com.alibaba.fastjson.JSONObject;
import com.lz.realtimepush.websocket.service.WebSocketServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/socket")
public class SocketOpsController {

    @Resource
    private WebSocketServer webSocket;

    @GetMapping(path = "publish")
    public String publish(String message, String userId) {
        //创建业务消息信息
        webSocket.sendOneMessage(userId, message);
        return "success";
    }
}

