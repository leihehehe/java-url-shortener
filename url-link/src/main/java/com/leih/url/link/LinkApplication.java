package com.leih.url.link;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.leih.url.link","com.leih.url.common"})
@EnableTransactionManagement
@EnableFeignClients
public class LinkApplication {
  public static void main(String[] args) {
      SpringApplication.run(LinkApplication.class,args);
  }
}
