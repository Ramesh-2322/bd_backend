package com.bloodbank.bdms.entity;

import com.bloodbank.bdms.entity.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "donor_id", nullable = false)
  private DonorProfile donor;

  @Column(nullable = false)
  private LocalDateTime scheduledAt;

  @Column(nullable = false)
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AppointmentStatus status;

  private String notes;

  @CreationTimestamp
  @Column(updatable = false)
  private Instant createdAt;
}
