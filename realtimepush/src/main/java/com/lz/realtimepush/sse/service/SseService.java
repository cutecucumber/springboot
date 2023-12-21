package com.lz.realtimepush.sse.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lizhao
 * @class SseService
 * @description
 * @create 2023/12/20 13:18
 */
@Service
@Slf4j
public class SseService {

    /**
     * 当前连接数
     */
    private static final AtomicInteger count = new AtomicInteger(0);

    /**
     * 使用map对象，便于根据userId来获取对应的SseEmitter，或者放redis里面
     */
    private static final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    /**
     * 创建用户连接并返回 SseEmitter
     *
     * @param userId 用户ID
     * @return SseEmitter
     */
    public static SseEmitter connect(String userId) {
        if (sseEmitterMap.containsKey(userId)) {
            return sseEmitterMap.get(userId);
        }
        try {
            //设置超时时间，0表示不过期。默认30秒
            SseEmitter sseEmitter = new SseEmitter(0L);
            sseEmitter.onCompletion(() -> {
                log.info("end connect: {}", userId);
                removeUser(userId);
            });
            sseEmitter.onError(e -> {
                log.info("error connect: {}", userId);
                removeUser(userId);
            });
            sseEmitter.onTimeout(() -> {
                log.info("timeout connect: {}", userId);
                removeUser(userId);
            });
            sseEmitterMap.put(userId, sseEmitter);

            //数量+1
            count.getAndIncrement();

            return sseEmitter;
        } catch (Exception e) {
            log.error("create sse connect error, userId: {}, errorMsg: {}", userId, e.getMessage(), e);
        }

        return null;
    }

    /**
     * 给指定用户发送消息
     *
     * @param userId
     * @param message
     */
    public static void sendMessage(String userId, String message) {
        if (sseEmitterMap.containsKey(userId)) {
            try {
                sseEmitterMap.get(userId).send(message);
            } catch (IOException e) {
                log.error("push error, userId: {}, errorMsg: {}", userId, e.getMessage(), e);
                removeUser(userId);
            }
        }
    }

    /**
     * 移除用户连接
     */
    public static void removeUser(String userId) {
        log.info("remove user: {}", userId);
        sseEmitterMap.remove(userId);
        //数量-1
        count.getAndDecrement();
    }
}
