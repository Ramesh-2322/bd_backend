package com.bdms.service;

public interface RealtimeNotificationService {

    void publish(String topic, Object payload);
}
