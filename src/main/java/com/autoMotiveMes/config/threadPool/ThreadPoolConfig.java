package com.autoMotiveMes.config.threadPool;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实现功能【线程池配置】
 *
 * @author li.hongyu
 * @date 2025-04-17 16:16:43
 */
@Configuration
public class ThreadPoolConfig {

    // 读取线程池配置
    @ConfigurationProperties(prefix = "thread-pool.scheduled")
    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        int coreSize = 20;  // 默认值（会被配置文件覆盖）
        String namePrefix = "scheduled-pool-";

        // 自定义线程工厂（用于命名线程）
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, namePrefix + threadNumber.getAndIncrement());
                thread.setDaemon(false);  // 非守护线程
                return thread;
            }
        };

        // 创建可调度线程池
        return new ScheduledThreadPoolExecutor(coreSize, threadFactory);
    }
}