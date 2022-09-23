package com.leih.url.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/***
 * Custom thread pool task executor
 * Use @Async("threadPoolTaskExecutor") to apply this executor to methods
 */
@Configuration
@EnableAsync
public class ThreadPoolTaskConfig {
    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //min thread pool size -> 8 threads
        executor.setCorePoolSize(16);
        //blocking queue
        executor.setQueueCapacity(1024);
        //if the blocking queue is full and the number of threads = core pool size, a new thread will be created to execute the task.
        //if the number of threads = max thread pool size and blocking queue is full, throw exception
        executor.setMaxPoolSize(64);
        //threads will be destroyed after the idle time > 30s (except the core pool size -> 8 threads)
        executor.setKeepAliveSeconds(30);
        executor.setThreadNamePrefix("Custom Thread Pool");
        //Reject policy
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }
}
