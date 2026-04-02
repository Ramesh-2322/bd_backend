package com.bdms.service.impl;

import com.bdms.dto.notification.NotificationResponse;
import com.bdms.entity.Donor;
import com.bdms.entity.Notification;
import com.bdms.repository.NotificationRepository;
import com.bdms.service.CurrentUserService;
import com.bdms.service.NotificationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    @Override
    public List<NotificationResponse> listMyNotifications() {
        Donor user = currentUserService.getCurrentUser();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
