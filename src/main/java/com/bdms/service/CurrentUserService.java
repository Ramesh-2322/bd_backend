package com.bdms.service;

import com.bdms.entity.Donor;

public interface CurrentUserService {

    Donor getCurrentUser();

    boolean isSuperAdmin();

    Long getCurrentHospitalId();
}
