package com.pawportal.backend.services.interfaces;

import com.pawportal.backend.models.responses.UserResponse;

import java.util.List;

public interface IUserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse suspendUser(Long id);

    UserResponse activateUser(Long id);

    void deleteUser(Long id);
}