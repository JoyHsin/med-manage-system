package org.me.joy.clinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置
 * 启用Spring的定时任务功能
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // 启用定时任务，用于会话清理等功能
}