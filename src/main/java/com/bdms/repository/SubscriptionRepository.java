package com.bdms.repository;

import com.bdms.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByHospitalIdOrderByStartDateDesc(Long hospitalId);

    Optional<Subscription> findByHospitalIdAndActiveTrue(Long hospitalId);
}
