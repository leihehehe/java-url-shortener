package com.leih.url.link.config;

import lombok.Data;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@Data
public class RabbitMQConfig {
  /***
   * Switch
   */
  private String shortLinkEventExchange = "short_link.event.exchange";
  /** ADD config */
  private String shortLinkAddLinkQueue = "short_link.add.link.queue";
  private String shortLinkAddMappingQueue = "short_link.add.mapping.queue";
  private String shortLinkAddRoutingKey = "short_link.add.link.mapping.routing.key";
  private String shortLinkAddLinkBindingKey = "short_link.add.link.*.routing.key";
  private String shortLinkAddMappingBindingKey = "short_link.add.*.mapping.routing.key";
  /** DELETE config */
  private String shortLinkDelLinkQueue = "short_link.del.link.queue";
  private String shortLinkDelMappingQueue = "short_link.del.mapping.queue";
  private String shortLinkDelRoutingKey = "short_link.del.link.mapping.routing.key";
  private String shortLinkDelLinkBindingKey = "short_link.del.link.*.routing.key";
  private String shortLinkDelMappingBindingKey = "short_link.del.*.mapping.routing.key";
  /** UPDATE config */
  private String shortLinkUpdateLinkQueue = "short_link.update.link.queue";
  private String shortLinkUpdateMappingQueue = "short_link.update.mapping.queue";
  private String shortLinkUpdateRoutingKey = "short_link.update.link.mapping.routing.key";
  private String shortLinkUpdateLinkBindingKey = "short_link.update.link.*.routing.key";
  private String shortLinkUpdateMappingBindingKey = "short_link.update.*.mapping.routing.key";

  //first queue
  private String shortLinkCheckDelayQueue = "short_link.check.delay.queue";
  private String shortLinkCheckQueue = "short_link.check.queue";
  private String shortLinkCheckDelayRoutingKey = "short_link.check.delay.routing.key";
  private String shortLinkCheckRoutingKey = "short_link.check.routing.key";

  /**
   * Usually one service one switch
   *
   * @return
   */
  @Bean
  public Exchange shortLinkEventExchange() {
    return new TopicExchange(shortLinkEventExchange, true, false);
  }

  /**
   * ADD CONFIG
   * @return
   */
  @Bean
  public Binding shortLinkAddLinkBinding() {
    return new Binding(
        shortLinkAddLinkQueue,
        Binding.DestinationType.QUEUE,
        shortLinkEventExchange,
        shortLinkAddLinkBindingKey,
        null);
  }

  @Bean
  public Binding shortLinkAddMappingBinding() {
    return new Binding(
        shortLinkAddMappingQueue,
        Binding.DestinationType.QUEUE,
        shortLinkEventExchange,
        shortLinkAddMappingBindingKey,
        null);
  }

  @Bean
  public Queue shortLinkAddLinkQueue() {
    return new Queue(shortLinkAddLinkQueue, true, false, false);
  }

  @Bean
  public Queue shortLinkAddMappingQueue() {
    return new Queue(shortLinkAddMappingQueue, true, false, false);
  }

  /**
   * DELETE CONFIG
   * @return
   */
  @Bean
  public Binding shortLinkDelLinkBinding() {
    return new Binding(
        shortLinkDelLinkQueue,
        Binding.DestinationType.QUEUE,
        shortLinkEventExchange,
        shortLinkDelLinkBindingKey,
        null);
  }

  @Bean
  public Binding shortLinkDelMappingBinding() {
    return new Binding(
        shortLinkDelMappingQueue,
        Binding.DestinationType.QUEUE,
        shortLinkEventExchange,
        shortLinkDelMappingBindingKey,
        null);
  }

  @Bean
  public Queue shortLinkDelLinkQueue() {
    return new Queue(shortLinkDelLinkQueue, true, false, false);
  }

  @Bean
  public Queue shortLinkDelMappingQueue() {
    return new Queue(shortLinkDelMappingQueue, true, false, false);
  }

  /**
   * UPDATE config
   * @return
   */
  @Bean
  public Binding shortLinkUpdateLinkBinding() {
    return new Binding(
            shortLinkUpdateLinkQueue,
            Binding.DestinationType.QUEUE,
            shortLinkEventExchange,
            shortLinkUpdateLinkBindingKey,
            null);
  }

  @Bean
  public Binding shortLinkUpdateMappingBinding() {
    return new Binding(
            shortLinkUpdateMappingQueue,
            Binding.DestinationType.QUEUE,
            shortLinkEventExchange,
            shortLinkUpdateMappingBindingKey,
            null);
  }
  @Bean
  public Queue shortLinkUpdateLinkQueue() {
    return new Queue(shortLinkUpdateLinkQueue, true, false, false);
  }

  @Bean
  public Queue shortLinkUpdateMappingQueue() {
    return new Queue(shortLinkUpdateMappingQueue, true, false, false);
  }

  private Integer ttl = 5000; //5 seconds
  /**
   * Dead letter queue
   * @return
   */
  @Bean
  public Queue shortLinkCheckDelayQueue(){
    HashMap<String, Object> args = new HashMap<>(3);
    args.put("x-message-ttl",ttl);
    args.put("x-dead-letter-exchange",shortLinkEventExchange);
    args.put("x-dead-letter-routing-key", shortLinkCheckRoutingKey);
    return new Queue(shortLinkCheckDelayQueue,true,false,false,args);
  }

  @Bean
  public Queue shortLinkCheckQueue(){
    return new Queue(shortLinkCheckQueue,true,false,false);
  }
  @Bean
  public Binding shortLinkCheckDelayBinding(){
    return new Binding(shortLinkCheckDelayQueue, Binding.DestinationType.QUEUE,shortLinkEventExchange,shortLinkCheckDelayRoutingKey,null);
  }

  @Bean
  public Binding shortCheckBinding(){
    return new Binding(shortLinkCheckQueue, Binding.DestinationType.QUEUE,shortLinkEventExchange,shortLinkCheckRoutingKey,null);
  }

}
