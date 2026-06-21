package com.scheduler.scheduler.aop;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class OrganizationFilterAspect {
    private final EntityManager entityManager;

    @Before("execution(* com.scheduler.scheduler.repository.*.*(..))")
    public void applyOrganizationFilter(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        if (signature.getMethod().isAnnotationPresent(SkipOrganizationFilter.class)) {
            return;
        }
        Long orgId = OrganizationContext.get();
        if (orgId != null) {
            entityManager.unwrap(org.hibernate.Session.class)
                    .enableFilter(OrganizationContext.FILTER_NAME)
                    .setParameter("orgId", orgId);
        }
    }
}
