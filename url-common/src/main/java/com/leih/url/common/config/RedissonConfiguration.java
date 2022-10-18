package com.leih.url.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfiguration {
  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private String redisPort;

//  @Value("${spring.redis.password}")
//  private String redisPass;

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config
        .useSingleServer()
//        .setPassword(redisPass)
        .setAddress("redis://" + redisHost + ":" + redisPort);
    RedissonClient redissonClient = Redisson.create(config);
    return redissonClient;
  }


/*  public RedissonClient redissonClusterClient() {
    Config config = new Config();
    config
        .useClusterServers()
        .setScanInterval(2000)
        .addNodeAddress("redis://127.0.0.1:7000")
        .addNodeAddress("redis://127.0.0.1:7002");
    RedissonClient redissonClient = Redisson.create(config);
    return redissonClient;
  }*/
}
