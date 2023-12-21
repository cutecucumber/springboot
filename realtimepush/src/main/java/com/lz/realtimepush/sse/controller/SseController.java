package com.lz.realtimepush.sse.controller;

import com.lz.realtimepush.sse.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/sse")
public class SseController {

    /**
     * sse 订阅消息
     */
    @GetMapping(path = "sub/{id}", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter sub(@PathVariable String id) {

        return SseService.connect(id);
    }

    /**
     * sse 发布消息
     */
    @GetMapping(path = "push")
    public void push(String id, String content) {
        SseService.sendMessage(id, content);
    }

    @GetMapping(path = "breakConnect")
    public void breakConnect(String id, HttpServletRequest request, HttpServletResponse response) {
        request.startAsync();
        SseService.removeUser(id);
    }
}

