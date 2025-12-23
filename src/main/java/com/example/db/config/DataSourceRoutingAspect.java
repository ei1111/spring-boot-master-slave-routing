package com.example.db.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Aspect
@Component
@Order(0)  // 트랜잭션보다 먼저 실행되도록
public class DataSourceRoutingAspect {

    @Around("@annotation(transactional)")
    public Object routeDataSource(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        try {
            if (transactional.readOnly()) {
                DataSourceContextHolder.set(DataSourceType.SLAVE);
                log.debug("Routing to SLAVE DataSource");
            } else {
                DataSourceContextHolder.set(DataSourceType.MASTER);
                log.debug("Routing to MASTER DataSource");
            }

            return joinPoint.proceed();

        } finally {
            DataSourceContextHolder.clear();
        }
    }
}