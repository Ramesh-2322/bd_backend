package com.bdms.service;

import com.bdms.dto.notification.NotificationResponse;

import java.util.List;

public interface NotificationQueryService {

    List<NotificationResponse> listMyNotifications();
}
