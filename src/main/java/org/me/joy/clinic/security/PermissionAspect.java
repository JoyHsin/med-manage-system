package org.me.joy.clinic.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.me.joy.clinic.exception.BusinessException;
import org.me.joy.clinic.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限控制切面
 * 处理 @RequiresPermission 注解的权限验证
 */
@Aspect
@Component
public class PermissionAspect {

    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);

    @Autowired
    private PermissionService permissionService;

    /**
     * 拦截带有 @RequiresPermission 注解的方法
     */
    @Around("@annotation(org.me.joy.clinic.security.RequiresPermission) || @within(org.me.joy.clinic.security.RequiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 首先检查方法级别的注解
        RequiresPermission methodAnnotation = AnnotationUtils.findAnnotation(method, RequiresPermission.class);
        RequiresPermission annotation = methodAnnotation;
        
        // 如果方法级别没有注解，检查类级别的注解
        if (annotation == null) {
            Class<?> targetClass = joinPoint.getTarget().getClass();
            annotation = AnnotationUtils.findAnnotation(targetClass, RequiresPermission.class);
        }
        
        if (annotation != null) {
            String[] requiredPermissions = annotation.value();
            RequiresPermission.LogicalOperator logical = annotation.logical();
            
            logger.debug("检查权限: {} (逻辑: {})", String.join(", ", requiredPermissions), logical);
            
            boolean hasPermission = false;
            
            if (logical == RequiresPermission.LogicalOperator.AND) {
                // 需要拥有所有权限
                hasPermission = permissionService.hasAllPermissions(requiredPermissions);
            } else {
                // 拥有其中一个权限即可
                hasPermission = permissionService.hasAnyPermission(requiredPermissions);
            }
            
            if (!hasPermission) {
                String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
                logger.warn("权限验证失败: 方法={}, 需要权限={}, 逻辑={}", 
                    methodName, String.join(", ", requiredPermissions), logical);
                throw new BusinessException("ACCESS_DENIED", "权限不足，无法访问该功能");
            }
            
            logger.debug("权限验证通过");
        }
        
        return joinPoint.proceed();
    }
}