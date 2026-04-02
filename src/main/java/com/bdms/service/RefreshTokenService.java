package com.bdms.service;

import com.bdms.entity.Donor;
import com.bdms.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(Donor user);

    RefreshToken validate(String token);

    void revoke(String token);
}
