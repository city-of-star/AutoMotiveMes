package com.automotivemes.common.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GlobalLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(GlobalLoggingAspect.class);

    // 定义一个通用的切入点，匹配所有的服务类方法
    @Pointcut("execution(* com.automotivemes.service.*.*(..))")
    public void serviceMethods() {}

    @Before("serviceMethods()")
    public void beforeServiceMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.info("开始执行服务方法 {}，参数：{}", methodName, args);
    }

    @After("serviceMethods()")
    public void afterServiceMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("服务方法 {} 执行结束", methodName);
    }
}
