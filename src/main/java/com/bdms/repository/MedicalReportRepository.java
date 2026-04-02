package com.bdms.repository;

import com.bdms.entity.MedicalReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {

    Page<MedicalReport> findByDonorId(Long donorId, Pageable pageable);
}
