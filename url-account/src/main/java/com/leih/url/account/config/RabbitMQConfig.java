package com.leih.url.account.config;

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


    /**
     * Restore plan
     */
    private String planRestoreDelayQueue="plan.restore.delay.queue";
    private String planRestoreDelayRoutingKey="plan.restore.delay.routing.key";
    private String planRestoreQueue = "plan.restore.queue";
    private String planRestoreRoutingKey = "plan.restore.routing.key";

    private Integer ttl = 60000; //1 mins
    /**
     * Dead letter queue
     * @return
     */
    @Bean
    public Queue planRestoreDelayQueue(){
        HashMap<String, Object> args = new HashMap<>(3);
        args.put("x-message-ttl",ttl);
        args.put("x-dead-letter-exchange",planEventExchange);
        args.put("x-dead-letter-routing-key", planRestoreRoutingKey);
        return new Queue(planRestoreDelayQueue,true,false,false,args);
    }

    @Bean
    public Queue planRestoreQueue(){
        return new Queue(planRestoreQueue,true,false,false);
    }
    @Bean
    public Binding planRestoreDelayBinding(){
        return new Binding(planRestoreDelayQueue, Binding.DestinationType.QUEUE,planEventExchange,planRestoreDelayRoutingKey,null);
    }

    @Bean
    public Binding planRestoreBinding(){
        return new Binding(planRestoreQueue, Binding.DestinationType.QUEUE,planEventExchange,planRestoreRoutingKey,null);
    }
}
