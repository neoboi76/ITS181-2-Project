package com.pawportal.backend.services;

import com.pawportal.backend.models.requests.LoginRequest;
import com.pawportal.backend.models.requests.RegisterRequest;
import com.pawportal.backend.models.requests.ResetRequest;
import com.pawportal.backend.models.requests.SettingsRequest;
import com.pawportal.backend.models.responses.LoginResponse;
import com.pawportal.backend.models.responses.RegisterResponse;
import com.pawportal.backend.models.responses.ResetResponse;
import com.pawportal.backend.models.responses.SettingsResponse;

public interface IAuthService {

    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    ResetResponse resetPassword(ResetRequest request);
    SettingsResponse updateUser(SettingsRequest request);
    SettingsRequest getUser(long id);
}

