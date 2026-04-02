package com.bdms.repository;

import com.bdms.entity.BloodRequest;
import com.bdms.entity.RequestStatus;
import com.bdms.entity.UrgencyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    List<BloodRequest> findAllByRequestedByIdOrderByCreatedAtDesc(Long requestedById);

    Optional<BloodRequest> findByIdAndRequestedById(Long id, Long requestedById);

    @Query("""
        select br from BloodRequest br
        where (:status is null or br.status = :status)
        and (:urgencyLevel is null or br.urgencyLevel = :urgencyLevel)
        and (:location is null or lower(br.location) like lower(concat('%', :location, '%')))
        """)
    Page<BloodRequest> searchAll(@Param("status") RequestStatus status,
                 @Param("urgencyLevel") UrgencyLevel urgencyLevel,
                 @Param("location") String location,
                 Pageable pageable);

        @Query("""
            select br from BloodRequest br
            where br.requestedBy.hospital.id = :hospitalId
            and (:status is null or br.status = :status)
            and (:urgencyLevel is null or br.urgencyLevel = :urgencyLevel)
            and (:location is null or lower(br.location) like lower(concat('%', :location, '%')))
            """)
        Page<BloodRequest> searchAllByHospital(@Param("hospitalId") Long hospitalId,
                           @Param("status") RequestStatus status,
                           @Param("urgencyLevel") UrgencyLevel urgencyLevel,
                           @Param("location") String location,
                           Pageable pageable);

    @Query("""
        select br from BloodRequest br
        where br.requestedBy.id = :requestedById
        and (:status is null or br.status = :status)
        and (:urgencyLevel is null or br.urgencyLevel = :urgencyLevel)
        and (:location is null or lower(br.location) like lower(concat('%', :location, '%')))
        """)
    Page<BloodRequest> searchMy(@Param("requestedById") Long requestedById,
                @Param("status") RequestStatus status,
                @Param("urgencyLevel") UrgencyLevel urgencyLevel,
                @Param("location") String location,
                Pageable pageable);

    long countByRequestedByHospitalId(Long hospitalId);

    List<BloodRequest> findByStatusAndCreatedAtBefore(RequestStatus status, LocalDateTime createdAt);
}
