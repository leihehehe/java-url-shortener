package com.leih.url.account.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQErrorConfig {
  private String planErrorExchange = "plan.error.exchange";
  private String planErrorQueue = "plan.error.queue";
  private String planErrorRoutingKey = "plan.error.routing.key";
  @Autowired private RabbitTemplate rabbitTemplate;

  @Bean
  public TopicExchange errorTopicExchange() {
    return new TopicExchange(planErrorExchange, true, false);
  }

  @Bean
  public Queue errorQueue() {
    return new Queue(planErrorQueue, true);
  }

  @Bean
  public Binding bindingErrorQueueAndExchange() {
    return BindingBuilder.bind(errorQueue())
        .to(errorTopicExchange())
        .with(planErrorRoutingKey);
  }

  @Bean
  public MessageRecoverer messageRecoverer() {
    return new RepublishMessageRecoverer(
        rabbitTemplate, planErrorExchange, planErrorRoutingKey);
  }
}
