package com.leih.url.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients
@EnableScheduling
@ComponentScan({"com.leih.url.account","com.leih.url.common"})
public class UrlAccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(UrlAccountApplication.class, args);
    }
}
