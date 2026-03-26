package com.bloodbank.bdms.repository;

import com.bloodbank.bdms.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
  List<Appointment> findByDonorId(Long donorId);
}
