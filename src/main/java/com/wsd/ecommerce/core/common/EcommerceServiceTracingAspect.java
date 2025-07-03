package com.wsd.ecommerce.core.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EcommerceServiceTracingAspect extends CommonTraceLoggerAspect {

    @Pointcut("execution(* com.wsd.ecommerce.presenter.api..*(..)))")
    public void ecommerceControllerAspect() {
    }

    @Pointcut("execution(* com.wsd.ecommerce.core.service..*.*(..)))")
    public void ecommerceServiceTrace() {
    }

    @Around("ecommerceServiceTrace()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        return trace(joinPoint);
    }

    @Around("ecommerceControllerAspect()")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return trace(joinPoint);
    }

}
