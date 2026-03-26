package com.bloodbank.bdms.dto.notification;

import com.bloodbank.bdms.entity.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class NotificationResponse {
  private Long id;
  private String title;
  private String message;
  private NotificationType type;
  private boolean read;
  private Instant createdAt;
}
