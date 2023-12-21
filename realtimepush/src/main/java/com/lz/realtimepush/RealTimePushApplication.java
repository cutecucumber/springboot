package com.lz.realtimepush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author lizhao
 * @class RealTimePushApplication
 * @description
 * @create 2023/12/20 13:17
 */
@SpringBootApplication
@EnableAsync
public class RealTimePushApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealTimePushApplication.class, args);
    }
}
