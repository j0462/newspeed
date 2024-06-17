package com.example.newspeed.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.example.newspeed.controller..*(..))")
    //시점
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        //다음 메서드로 진행됨
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.info("Request URL: " + request.getRequestURL());
            log.info("HTTP Method : " + request.getMethod());

        } else {
            log.info("인가 처리 안됨");
        }
        return joinPoint.proceed();
    }
}