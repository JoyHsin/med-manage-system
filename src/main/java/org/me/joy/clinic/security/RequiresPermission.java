package org.me.joy.clinic.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限控制注解
 * 用于标记需要特定权限才能访问的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    
    /**
     * 需要的权限代码
     * 支持多个权限，满足其中一个即可访问
     */
    String[] value();
    
    /**
     * 权限逻辑关系
     * AND: 需要同时拥有所有权限
     * OR: 拥有其中一个权限即可（默认）
     */
    LogicalOperator logical() default LogicalOperator.OR;
    
    /**
     * 权限逻辑操作符
     */
    enum LogicalOperator {
        AND, OR
    }
}