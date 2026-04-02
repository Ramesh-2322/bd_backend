package com.bdms.service;

import com.bdms.dto.auth.AuthResponse;
import com.bdms.dto.auth.LoginRequest;
import com.bdms.dto.auth.RefreshTokenRequest;
import com.bdms.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}
