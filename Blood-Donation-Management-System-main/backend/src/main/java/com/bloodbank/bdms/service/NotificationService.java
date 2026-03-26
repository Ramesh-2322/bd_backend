package com.bloodbank.bdms.service;

import com.bloodbank.bdms.dto.notification.NotificationResponse;
import com.bloodbank.bdms.entity.Notification;
import com.bloodbank.bdms.entity.User;
import com.bloodbank.bdms.entity.enums.NotificationType;
import com.bloodbank.bdms.entity.enums.RoleName;
import com.bloodbank.bdms.exception.NotFoundException;
import com.bloodbank.bdms.repository.NotificationRepository;
import com.bloodbank.bdms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
    this.notificationRepository = notificationRepository;
    this.userRepository = userRepository;
  }

  public List<NotificationResponse> listForUser(Long userId) {
    return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public void notifyUser(User user, String title, String message, NotificationType type) {
    Notification notification = Notification.builder()
        .user(user)
        .title(title)
        .message(message)
        .type(type)
        .read(false)
        .build();
    notificationRepository.save(notification);
  }

  @Transactional
  public void notifyRole(RoleName role, String title, String message, NotificationType type) {
    List<User> users = userRepository.findByRolesName(role);
    users.forEach(user -> notifyUser(user, title, message, type));
  }

  @Transactional
  public NotificationResponse markRead(Long id) {
    Notification notification = notificationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Notification not found"));
    notification.setRead(true);
    return toResponse(notification);
  }

  private NotificationResponse toResponse(Notification notification) {
    return NotificationResponse.builder()
        .id(notification.getId())
        .title(notification.getTitle())
        .message(notification.getMessage())
        .type(notification.getType())
        .read(notification.isRead())
        .createdAt(notification.getCreatedAt())
        .build();
  }
}
