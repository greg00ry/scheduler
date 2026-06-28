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
        if (isCallerAnnotatedWithSkip()) {
            return;
        }
        Long orgId = OrganizationContext.get();
        if (orgId != null) {
            entityManager.unwrap(org.hibernate.Session.class)
                    .enableFilter(OrganizationContext.FILTER_NAME)
                    .setParameter("orgId", orgId);
        }
    }

    private boolean isCallerAnnotatedWithSkip() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            try {
                Class<?> clazz = Class.forName(element.getClassName());
                for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals(element.getMethodName())
                            && method.isAnnotationPresent(SkipOrganizationFilter.class)) {
                        return true;
                    }
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        return false;
    }
}
