package com.leih.url.account.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RabbitMQConfig {
    @Bean
    public MessageConverter messageConverter(){
        return  new Jackson2JsonMessageConverter();
    }

    private String planEventExchange = "plan.event.exchange";
    private String planFreeInitQueue = "plan.free_init.queue";
    private String planFreeInitRoutingKey = "plan.free_init.routing.key";

    @Bean
    public Exchange planEventExchange(){
        return new TopicExchange(planEventExchange,true,false);
    }
    @Bean
    public Binding planFreeInitBinding(){
        return new Binding(planFreeInitQueue, Binding.DestinationType.QUEUE,planEventExchange,planFreeInitRoutingKey,null);
    }
    @Bean
    public Queue planFreeInitQueue(){
        return new Queue(planFreeInitQueue,true,false,false);
    }

}
