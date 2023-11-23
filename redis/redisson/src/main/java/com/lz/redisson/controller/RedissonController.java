package com.lz.redisson.controller;

import com.lz.redisson.service.RedissonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lizhao
 * @class RedissonController
 * @description
 * @create 2023/11/22 15:34
 */
@RestController
@RequestMapping
public class RedissonController {

    private final RedissonService redissonService;

    public RedissonController(RedissonService redissonService) {
        this.redissonService = redissonService;
    }

    @GetMapping("/lock")
    public void lock(String key) {
        redissonService.lockWithoutLeaseTime(key);
    }

    @GetMapping("/lockTest")
    public void lockTest(String key) {
        redissonService.lockWithoutLeaseTimeTest(key);
    }
}
