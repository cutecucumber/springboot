package com.lz.realtimepush.base.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lizhao
 * @class BaseController
 * @description
 * @create 2023/12/20 15:04
 */
@Controller
@RequestMapping
public class BaseController {

    /**
     * sse 页面
     *
     */
    @RequestMapping("/sse/index")
    public String sse() {
        return "sse";
    }

    @RequestMapping("/socket/index")
    public String socket() {
        return "socket";
    }
}
