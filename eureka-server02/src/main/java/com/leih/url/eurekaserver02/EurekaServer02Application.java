package com.leih.url.eurekaserver02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServer02Application {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServer02Application.class, args);
    }
}
