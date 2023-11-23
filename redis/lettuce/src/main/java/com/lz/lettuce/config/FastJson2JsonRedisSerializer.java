package com.lz.lettuce.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

/**
 * @author lizhao
 * @class FastJson2JsonRedisSerializer
 * @description
 * @create 2023/11/23 11:36
 */
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {

    private final Class<T> clazz;

    public FastJson2JsonRedisSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    static {
        //解决autoType is not support问题
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }

        //不序列化类名称（SerializerFeature.WriteClassName）
        return JSON.toJSONString(t).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return JSON.parseObject(new String(bytes), clazz);
    }
}
