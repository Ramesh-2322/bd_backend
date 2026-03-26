package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.notification.NotificationResponse;
import com.bloodbank.bdms.service.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF','DONOR')")
  public List<NotificationResponse> listForUser(@PathVariable Long userId) {
    return notificationService.listForUser(userId);
  }

  @PatchMapping("/{id}/read")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF','DONOR')")
  public NotificationResponse markRead(@PathVariable Long id) {
    return notificationService.markRead(id);
  }
}
