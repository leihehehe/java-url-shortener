package com.leih.url.link.config;

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
                .addPathPatterns("/api/link/*/**","/api/group/*/**","/api/domain/*/**")
                .excludePathPatterns("/api/link/*/check");
    }
}
