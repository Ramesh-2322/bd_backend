package com.bdms.security;

public final class TenantContext {

    private static final ThreadLocal<Long> HOSPITAL_ID = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void setHospitalId(Long hospitalId) {
        HOSPITAL_ID.set(hospitalId);
    }

    public static Long getHospitalId() {
        return HOSPITAL_ID.get();
    }

    public static void clear() {
        HOSPITAL_ID.remove();
    }
}
