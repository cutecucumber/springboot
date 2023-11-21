package com.lz.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author lizhao
 * @class QueueConfig
 * @description 队列配置类
 * @create 2023/11/16 15:37
 */
@Configuration
public class QueueConfig {

    public static final String BUSINESS_QUEUE_NAME = "Business_Queue";
    public static final String BUSINESS_EXCHANGE_NAME = "Business_Exchange";
    public static final String BUSINESS_ROUTING_KEY = "Business_Key";
    public static final String Dlx_QUEUE_NAME = "Dlx_Queue";
    public static final String DLX_EXCHANGE_NAME = "Dlx_Exchange";
    public static final String Dlx_ROUTING_KEY = "Dlx_Key";

    private final RabbitTemplate rabbitTemplate;

    public QueueConfig(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 声明业务队列，并绑定死信交换机
     *
     * @return
     */
    @Bean("businessQueue")
    public Queue businessQueue() {
        HashMap<String, Object> map = new HashMap<>(8);
        map.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        //业务队列绑定死信交换机的routingKey和死信队列绑定死信交换机的routingKey保持一致
        map.put("x-dead-letter-routing-key", Dlx_ROUTING_KEY);
        //该队列上的所有消息过期时间
        map.put("x-message-ttl", 30_000);

        return new Queue(BUSINESS_QUEUE_NAME, true, false, false, map);
    }

    /**
     * 声明业务交换机
     *
     * @return
     */
    @Bean(name = "businessExchange")
    public TopicExchange businessExchange() {
        return new TopicExchange(BUSINESS_EXCHANGE_NAME, true, false);
    }

    /**
     * 业务队列和业务交换机绑定
     *
     * @param businessQueue
     * @param businessExchange
     * @return
     */
    @Bean(name = "businessBinding")
    public Binding businessBinding(@Qualifier("businessQueue") Queue businessQueue,
                                   @Qualifier("businessExchange") TopicExchange businessExchange) {
        return BindingBuilder.bind(businessQueue).to(businessExchange).with(BUSINESS_ROUTING_KEY);
    }

    /**
     * 声明死信队列
     *
     * @return
     */
    @Bean(name = "dlxQueue")
    public Queue dlxQueue() {
        return new Queue(Dlx_QUEUE_NAME, true);
    }

    /**
     * 声明死信交换机
     *
     * @return
     */
    @Bean(name = "dlxExchange")
    public TopicExchange dlxExchange() {
        return new TopicExchange(DLX_EXCHANGE_NAME, true, false);
    }

    /**
     * 死信队列和死信交换机绑定
     *
     * @param dlxQueue
     * @param dlxExchange
     * @return
     */
    @Bean(name = "dlxBinding")
    public Binding dlxBinding(@Qualifier("dlxQueue") Queue dlxQueue,
                              @Qualifier("dlxExchange") TopicExchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with(Dlx_ROUTING_KEY);
    }

    /**
     * 重试结束之后的策略，重新放入到队列中；默认是丢弃，如果绑定了死信队列，则进入死信中
     */
//    @Bean
//    public MessageRecoverer messageRecoverer(){
//        return new RepublishMessageRecoverer(rabbitTemplate, DLX_EXCHANGE_NAME, Dlx_ROUTING_KEY);
//    }
}
