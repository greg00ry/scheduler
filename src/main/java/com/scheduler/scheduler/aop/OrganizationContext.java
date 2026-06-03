package com.scheduler.scheduler.aop;

public class OrganizationContext {
    public static final String FILTER_NAME = "organizationFilter";
    private static final ThreadLocal<Long> organizationId = new ThreadLocal<>();

    public static void set(Long id) { organizationId.set(id); }
    public static Long get() { return organizationId.get(); }
    public static void clear() { organizationId.remove(); }
}
