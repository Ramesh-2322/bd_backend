package com.bdms.repository;

import com.bdms.entity.Appointment;
import com.bdms.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        List<Appointment> findAllByDonorIdOrderByAppointmentDateDesc(Long donorId);

    @Query("""
            select a from Appointment a
            where (:status is null or a.status = :status)
            and (:hospitalName is null or lower(a.hospitalName) like lower(concat('%', :hospitalName, '%')))
            """)
    Page<Appointment> searchAll(@Param("status") AppointmentStatus status,
                                @Param("hospitalName") String hospitalName,
                                Pageable pageable);

    @Query("""
            select a from Appointment a
            where a.donor.hospital.id = :hospitalId
            and (:status is null or a.status = :status)
            and (:hospitalName is null or lower(a.hospitalName) like lower(concat('%', :hospitalName, '%')))
            """)
    Page<Appointment> searchAllByHospital(@Param("hospitalId") Long hospitalId,
                                          @Param("status") AppointmentStatus status,
                                          @Param("hospitalName") String hospitalName,
                                          Pageable pageable);

    @Query("""
            select a from Appointment a
            where a.donor.id = :donorId
            and (:status is null or a.status = :status)
            and (:hospitalName is null or lower(a.hospitalName) like lower(concat('%', :hospitalName, '%')))
            """)
    Page<Appointment> searchMy(@Param("donorId") Long donorId,
                               @Param("status") AppointmentStatus status,
                               @Param("hospitalName") String hospitalName,
                               Pageable pageable);

    boolean existsByDonorIdAndAppointmentDateAndStatusIn(Long donorId,
                                                          LocalDateTime appointmentDate,
                                                          List<AppointmentStatus> statuses);

    long countByStatus(AppointmentStatus status);

        long countByStatusAndAppointmentDateBetween(AppointmentStatus status,
                                                                                                LocalDateTime startDate,
                                                                                                LocalDateTime endDate);

        long countByDonorHospitalIdAndStatus(Long hospitalId, AppointmentStatus status);
}
