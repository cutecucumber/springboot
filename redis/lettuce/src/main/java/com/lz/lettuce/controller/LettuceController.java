package com.lz.lettuce.controller;

import com.lz.lettuce.entity.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author lizhao
 * @class LettuceController
 * @description lettuce客户端集成spring-aop机制的cache实现
 * @create 2023/11/22 15:34
 */
@RestController
@RequestMapping
public class LettuceController {

    private final RedisTemplate<String, Object> redisTemplate;

    public LettuceController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * redis中查询的key = key::param的值，如果redis中存在，则不执行业务代码，如果redis中不存在，则执行业务代码
     *
     * @param param
     * @return
     */
    @GetMapping("/test1")
    @Cacheable(cacheNames = "key")
    public String lock(String param) {
        System.out.println("test1");
        return "value1";
    }

    /**
     * 先执行业务代码，将方法返回值作为redis的value值，key = key::1, 存入redis中
     *
     * @param param
     * @return
     */
    @GetMapping("/test2")
    @CachePut(cacheNames = "key", key = "#param = 1")
    public User test2(String param) {
        System.out.println("test2");
        User user = new User();
        user.setUsername("bbb");
        user.setId("aaa");
        return user;
    }

    /**
     * 删除redis中key = key::param的对象
     *
     * @param param
     * @return
     */
    @GetMapping("/test3")
    @CacheEvict(cacheNames = "key")
    public String test3(String param) {
        System.out.println("test3");
        return "value3";
    }

    @GetMapping("/test4")
    public void test4() {
        User user = new User();
        user.setId("111");
        user.setUsername("222");
        redisTemplate.opsForValue().set("user", user, 30, TimeUnit.SECONDS);
    }
}
