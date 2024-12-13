package com.github.therenegade.notification.manager.config;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Configuration
@EnableScheduling
public class ApplicationConfig {

    @Value("${scheduler.notifications.timestamp.threadPoolSize}")
    private int timestampNotificationsThreadPoolSize;

    @Value("${scheduler.notifications.cron.threadPoolSize}")
    private int cronNotificationsThreadPoolSize;


    @Bean("telegramSendNotificationsExecutor")
    public ExecutorService telegramSendNotificationsExecutor() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("TelegramSendNotificationsExecutor-%d")
                .build();
        return Executors.newFixedThreadPool(timestampNotificationsThreadPoolSize, threadFactory);
    }

    @Bean("timestampNotificationsScheduledExecutor")
    public ScheduledExecutorService timestampNotificationsScheduledExecutor() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("TimestampNotificationsScheduledExecutor-%d")
                .build();
        return Executors.newScheduledThreadPool(timestampNotificationsThreadPoolSize, threadFactory);
    }

    @Bean("cronNotificationsScheduledExecutor")
    public ScheduledExecutorService cronNotificationsScheduledExecutor() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("CronNotificationsScheduledExecutor-%d")
                .build();
        return Executors.newScheduledThreadPool(cronNotificationsThreadPoolSize, threadFactory);
    }
}
