package com.pawportal.backend.services;

import com.pawportal.backend.models.*;
import com.pawportal.backend.models.enums.Role;
import com.pawportal.backend.models.requests.LoginRequest;
import com.pawportal.backend.models.requests.RegisterRequest;
import com.pawportal.backend.models.requests.ResetRequest;
import com.pawportal.backend.models.requests.SettingsRequest;
import com.pawportal.backend.models.responses.LoginResponse;
import com.pawportal.backend.models.responses.RegisterResponse;
import com.pawportal.backend.models.responses.ResetResponse;
import com.pawportal.backend.models.responses.SettingsResponse;
import com.pawportal.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService, UserDetailsService  {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already exists");
        }

        UserModel user = new UserModel();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        UserModel savedUser = userRepository.save(user);


        return new RegisterResponse("Account Created!", savedUser.getEmail(), savedUser.getUserId());
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after successful authentication."));
        String token = jwtTokenProvider.generateToken(user.getEmail());

        return new LoginResponse("Login Successful!", token, user.getEmail(), user.getFirstName(),
                user.getLastName(), user.getGender(), user.getCountry(), user.getLanguage(), user.getUserId());
    }

    @Override
    public ResetResponse resetPassword(ResetRequest request) {
        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ResetResponse("Password has been reset successfully.");
    }

    @Override
    public SettingsResponse updateUser(SettingsRequest request) {


        UserModel user = userRepository.findById(request.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setCountry(request.getCountry());
        user.setLanguage(request.getLanguage());

        userRepository.save(user);

        return new SettingsResponse("User information succesfully updated");

    }

    @Override
    public SettingsRequest getUser(long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new SettingsRequest(user.getFirstName(), user.getLastName(), user.getUserId(),
                user.getGender(), user.getCountry(), user.getLanguage());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}