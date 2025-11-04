package com.pawportal.backend.services.interfaces;

import com.pawportal.backend.models.requests.*;
import com.pawportal.backend.models.responses.*;

/*
    Developed by Group 6:
        Kenji Mark Alan Arceo
        Carl Norbi Felonia
        Ryonan Owen Ferrer
        Dino Alfred Timbol
        Mike Emil Vocal

 */

//Interface for the auth service. Promotes loose coupling
//and dependency injection

public interface IAuthService {

    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    ResetResponse resetPassword(ResetRequest request);
    SettingsResponse updateUser(SettingsRequest request);
    SettingsRequest getUser(long id);
    Long getUserIdByEmail(String email);
    ResetResponse requestReset(String email);
    boolean isEmailValid(String email);
    ResetResponse forgotPassword(ForgotRequest request);
    ForgotResponse requestForgot(String email);
}

