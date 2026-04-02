package com.bdms.repository;

import com.bdms.entity.Donor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DonorRepository extends JpaRepository<Donor, Long> {
    Optional<Donor> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Donor> findAllByBloodGroupAndAvailabilityStatusTrue(String bloodGroup);

    @Query("""
            select d from Donor d
            where (:bloodGroup is null or lower(d.bloodGroup) = lower(:bloodGroup))
            and (:location is null or lower(d.location) like lower(concat('%', :location, '%')))
            and (:availabilityStatus is null or d.availabilityStatus = :availabilityStatus)
            """)
    Page<Donor> searchDonors(@Param("bloodGroup") String bloodGroup,
                             @Param("location") String location,
                             @Param("availabilityStatus") Boolean availabilityStatus,
                             Pageable pageable);

        @Query("""
            select d from Donor d
            where d.hospital.id = :hospitalId
            and (:bloodGroup is null or lower(d.bloodGroup) = lower(:bloodGroup))
            and (:location is null or lower(d.location) like lower(concat('%', :location, '%')))
            and (:availabilityStatus is null or d.availabilityStatus = :availabilityStatus)
            """)
        Page<Donor> searchDonorsByHospital(@Param("hospitalId") Long hospitalId,
                           @Param("bloodGroup") String bloodGroup,
                           @Param("location") String location,
                           @Param("availabilityStatus") Boolean availabilityStatus,
                           Pageable pageable);

    long countByAvailabilityStatusTrue();

    long countByLastDonationDateBetween(LocalDate startDate, LocalDate endDate);

    long countByHospitalId(Long hospitalId);

    long countByHospitalIdAndAvailabilityStatusTrue(Long hospitalId);
}
