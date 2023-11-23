package com.lz.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * @author lizhao
 * @class BaseConfig
 * @description
 * @create 2023/11/21 19:12
 */
@Configuration
public class BaseConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() throws IOException {
        Config config = Config.fromYAML(new ClassPathResource("redisson.yml").getInputStream());
        return Redisson.create(config);
    }
}
