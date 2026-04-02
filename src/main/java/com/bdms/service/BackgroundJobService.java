package com.bdms.service;

public interface BackgroundJobService {

    void cleanupExpiredRequests();
}
