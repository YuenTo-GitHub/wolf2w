package cn.wolfcode.wolf2w.article.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Data
@Configuration
@ConfigurationProperties(prefix = "thread")
public class ThreadConfig {
    private int corePoolSize;

    private int maximumPoolSize;

    private int keepAliveTime;

    private int workQueueCapacity;

    @Bean
    public ThreadPoolExecutor businessThreadPoolExecutor(){
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingDeque<>(workQueueCapacity));
    }
}