package com.github.therenegade.notification.manager.config;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
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

    private static final int CRON_NOTIFICATIONS_THREAD_NUMBER = 10;
    private static final int TIMESTAMP_NOTIFICATIONS_THREAD_NUMBER = 10;

    private static final int TELEGRAM_SEND_NOTIFICATION_THREAD_POOL_NUMBER = 25;

    @Bean("telegramSendNotificationsExecutor")
    public ExecutorService telegramSendNotificationsExecutor() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("TelegramSendNotificationsExecutor-%d")
                .build();
        return Executors.newFixedThreadPool(TELEGRAM_SEND_NOTIFICATION_THREAD_POOL_NUMBER, threadFactory);
    }

    @Bean("timestampNotificationsScheduledExecutor")
    public ScheduledExecutorService timestampNotificationsScheduledExecutor() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("TimestampNotificationsScheduledExecutor-%d")
                .build();
        return Executors.newScheduledThreadPool(TIMESTAMP_NOTIFICATIONS_THREAD_NUMBER, threadFactory);
    }

    @Bean("cronNotificationsScheduledExecutor")
    public ScheduledExecutorService cronNotificationsScheduledExecutor() {
        ThreadFactory threadFactory = new BasicThreadFactory.Builder()
                .namingPattern("CronNotificationsScheduledExecutor-%d")
                .build();
        return Executors.newScheduledThreadPool(CRON_NOTIFICATIONS_THREAD_NUMBER, threadFactory);
    }
}
