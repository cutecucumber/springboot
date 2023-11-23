package com.lz.lettuce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author lizhao
 * @class RedissonApplication
 * @description
 * @create 2023/11/21 19:09
 */
@SpringBootApplication
@EnableCaching
public class LettuceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LettuceApplication.class, args);
    }
}
