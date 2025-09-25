package com.ohgoodteam.ohgoodpay.scheduled.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class ScheduledConfig {
    
    /**
     * 스케줄러 설정
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10); // 원하는 스레드 수
        scheduler.setThreadNamePrefix("my-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true); // 종료 대기 여부
        scheduler.setAwaitTerminationSeconds(30); // 대기 시간
        return scheduler;
    }
    
}
