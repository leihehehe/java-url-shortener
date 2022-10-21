package com.leih.url.shop.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Data
public class RabbitMQConfig {
    /**
     * Exchange
     */
    private String orderEventExchange="order.event.exchange";
    /**
     * Delayed queue
     */
    private String orderCloseDelayQueue = "order.close.delay.queue";
    /**
     * Dead letter queue
     */
    private String orderCloseQueue = "order.close.queue";
    private String orderCloseDelayRoutingKey="order.close.delay.routing.key";
    private String orderCloseRoutingKey="order.close.routing.key";
    /**
     * Expire: 1 mins
     */
    private Integer ttl = 1000*60;

    /**
     * Message converter
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
      return  new Jackson2JsonMessageConverter();
    }
    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange(orderEventExchange,true,false);
    }
    @Bean
    public Queue orderCloseDelayQueue(){
        Map<String,Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange",orderEventExchange);
        args.put("x-dead-letter-routing-key",orderCloseRoutingKey);
        args.put("x-message-ttl",ttl);
        return new Queue(orderCloseDelayQueue,true,false,false,args);
    }

    /**
     * Dead letter queue
     * @return
     */
    @Bean
    public Queue orderCloseQueue(){
        return new Queue(orderCloseQueue,true,false,false);
    }
    @Bean
    public Binding orderCloseDelayBinding(){
        return new Binding(orderCloseDelayQueue, Binding.DestinationType.QUEUE,orderEventExchange,orderCloseDelayRoutingKey,null);
    }
    @Bean
    public Binding orderCloseBinding(){
        return new Binding(orderCloseQueue, Binding.DestinationType.QUEUE,orderEventExchange,orderCloseRoutingKey,null);
    }

    /***
     * Update order status
     */
    private String orderUpdateQueue="order.update.queue";
    /**
     * Update account's plans
     */
    private String orderPlanQueue = "order.plan.queue";
    /**
     * this will be used after successful payment
     */
    private String orderUpdatePlanRoutingKey = "order.update.plan.routing.key";
    private String orderUpdateBindingKey = "order.update.*.routing.key";
    private String orderPlanBindingKey = "order.*.plan.routing.key";

    @Bean
    public Queue orderUpdateQueue(){
        return new Queue(orderUpdateQueue,true,false,false);
    }

    /**
     * Dead letter queue
     * @return
     */
    @Bean
    public Queue orderPlanQueue(){
        return new Queue(orderPlanQueue,true,false,false);
    }
    @Bean
    public Binding orderUpdateBinding(){
        return new Binding(orderUpdateQueue, Binding.DestinationType.QUEUE,orderEventExchange,orderUpdatePlanRoutingKey,null);
    }
    @Bean
    public Binding orderPlanBinding(){
        return new Binding(orderPlanQueue, Binding.DestinationType.QUEUE,orderEventExchange,orderUpdatePlanRoutingKey,null);
    }
}
