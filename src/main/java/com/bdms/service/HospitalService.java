package com.bdms.service;

import com.bdms.dto.hospital.HospitalRequest;
import com.bdms.dto.hospital.HospitalResponse;

import java.util.List;

public interface HospitalService {

    HospitalResponse create(HospitalRequest request);

    HospitalResponse update(Long id, HospitalRequest request);

    List<HospitalResponse> getAll();
}
