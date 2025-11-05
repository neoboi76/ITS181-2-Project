package com.pawportal.backend.services.implementations;

import com.pawportal.backend.models.*;
import com.pawportal.backend.models.enums.Role;
import com.pawportal.backend.models.requests.*;
import com.pawportal.backend.models.responses.*;
import com.pawportal.backend.repositories.PasswordTokenRepository;
import com.pawportal.backend.repositories.UserRepository;
import com.pawportal.backend.services.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/*
    Developed by Group 6:
        Kenji Mark Alan Arceo
        Carl Norbi Felonia
        Ryonan Owen Ferrer
        Dino Alfred Timbol
        Mike Emil Vocal

 */

//auth service Contains business logic
//for audit log user operations

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService, UserDetailsService  {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final ForgotService forgotService;
    private final PasswordTokenRepository passwordTokenRepository;


    //Registers a user
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

        if (request.getEmail().equals("admin@gmail.com")) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }

        UserModel savedUser = userRepository.save(user);

        return new RegisterResponse("Account Created!", savedUser.getEmail(), savedUser.getUserId());
    }

    //Authenticates a user
    @Override
    public LoginResponse login(LoginRequest request) {
        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after successful authentication."));

        if (user.getSuspended() != null && user.getSuspended()) {
            throw new RuntimeException("Account has been suspended. Please contact support.");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail());

        return new LoginResponse(
                "Login Successful!",
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getMobileNumber(),
                user.getGender(),
                user.getCountry(),
                user.getLanguage(),
                user.getRole().name(),
                user.getUserId()
        );
    }

    //Resets password of a user (from the settings page)
    @Override
    public ResetResponse resetPassword(ResetRequest request) {

        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ResetResponse("Password has been reset successfully.");
    }

    //Resets a password (from the log in page)
    @Override
    public ResetResponse forgotPassword(ForgotRequest request) {

        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        PasswordResetToken tokenRecord = passwordTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (tokenRecord.getExpiry().isBefore(LocalDateTime.now())) {
            return new ResetResponse("Token expired");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordTokenRepository.delete(tokenRecord);

        return new ResetResponse("Password has been reset successfully.");
    }

    //Requests reset password (from the settings page)
    @Override
    public ResetResponse requestReset(String email) {
        Optional<UserModel> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();
            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusHours(1);

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiry(expiry);
            passwordTokenRepository.save(resetToken);

            emailService.sendResetPasswordEmail(email, token);

            return new ResetResponse("If your email exists, a reset link has been sent.");
        }

        return null;
    }

    //Requests forgot password (from the login page)
    @Override
    public ForgotResponse requestForgot(String email) {
        Optional<UserModel> optionalUser = userRepository.findByEmail(email);

        System.out.println(optionalUser.isPresent());

        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();
            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusHours(1);

            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiry(expiry);
            passwordTokenRepository.save(resetToken);

            forgotService.sendResetPasswordEmail(email, token);

            return new ForgotResponse("If your email exists, a reset link has been sent.");
        }

        return null;
    }

    //Updates user information
    @Override
    public SettingsResponse updateUser(SettingsRequest request) {
        UserModel user = userRepository.findById(request.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMobileNumber(request.getMobileNumber());
        user.setGender(request.getGender());
        user.setCountry(request.getCountry());
        user.setLanguage(request.getLanguage());

        userRepository.save(user);

        return new SettingsResponse("User information successfully updated");
    }

    //Retrieves a particular user
    @Override
    public SettingsRequest getUser(long id) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new SettingsRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getUserId(),
                user.getMobileNumber(),
                user.getGender(),
                user.getCountry(),
                user.getLanguage()
        );
    }

    //Loads user by username. Used by security config via UserDetailsService
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

    //Retrieves a particular user by email
    @Override
    public Long getUserIdByEmail(String email) {
        return userRepository.getUserIdByEmail(email);
    }

    //Checks whether an email exists in the db
    @Override
    public boolean isEmailValid(String email) {
        return userRepository.existsByEmail(email);
    }
}