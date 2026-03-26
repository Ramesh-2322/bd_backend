package com.bloodbank.bdms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "actor_id")
  private User actor;

  @Column(nullable = false)
  private String action;

  @Column(nullable = false)
  private String entityType;

  private Long entityId;

  @Column(length = 4000)
  private String metadata;

  @CreationTimestamp
  @Column(updatable = false)
  private Instant createdAt;
}
