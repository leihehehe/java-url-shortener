package com.leih.url.account.config;

import com.leih.url.common.intercepter.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                //add paths to be intercepted
                .addPathPatterns("/api/account/*/**","/api/plan/*/**")
                .excludePathPatterns("/api/account/*/register","/api/account/*/login",
                        "/api/notification/*/captcha","/api/notification/*/send_code","/api/plan/*/use");
    }
}
