package com.leih.url.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.leih.url.account","com.leih.url.common"})
public class UrlAccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(UrlAccountApplication.class, args);
    }
}
