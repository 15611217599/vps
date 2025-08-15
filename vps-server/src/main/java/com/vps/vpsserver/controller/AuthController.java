package com.vps.vpsserver.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vps.vpsserver.dto.AuthResponse;
import com.vps.vpsserver.dto.ForgotPasswordRequest;
import com.vps.vpsserver.dto.LoginRequest;
import com.vps.vpsserver.dto.RegisterRequest;
import com.vps.vpsserver.dto.ResetPasswordRequest;
import com.vps.vpsserver.dto.UpdateProfileRequest;
import com.vps.vpsserver.entity.User;
import com.vps.vpsserver.service.MessageService;
import com.vps.vpsserver.service.UserService;
import com.vps.vpsserver.util.JwtUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final MessageService messageService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh-CN") String acceptLanguage) {
        try {
            User user = userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword(), acceptLanguage);
            UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail()));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh-CN") String acceptLanguage) {
        try {
            userService.authenticateUser(request.getUsername(), request.getPassword(), acceptLanguage);
            UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
            String token = jwtUtil.generateToken(userDetails);
            User user = (User) userDetails;

            return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail()));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh-CN") String acceptLanguage) {
        try {
            String jwt = token.substring(7); // 移除 "Bearer " 前缀
            String username = jwtUtil.extractUsername(jwt);
            UserDetails userDetails = userService.loadUserByUsername(username);
            User user = (User) userDetails;

            Map<String, Object> profile = new HashMap<>();
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());
            profile.put("createdAt", user.getCreatedAt());

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Locale locale = messageService.parseLocale(acceptLanguage);
            Map<String, String> error = new HashMap<>();
            error.put("error", messageService.getMessage("error.profile.fetch.failed", locale));
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdateProfileRequest request,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh-CN") String acceptLanguage) {
        try {
            String jwt = token.substring(7); // 移除 "Bearer " 前缀
            String currentUsername = jwtUtil.extractUsername(jwt);

            User updatedUser = userService.updateUserProfile(
                    currentUsername,
                    request.getUsername(),
                    request.getEmail(),
                    request.getCurrentPassword(),
                    request.getNewPassword(),
                    acceptLanguage
            );

            Map<String, Object> profile = new HashMap<>();
            profile.put("username", updatedUser.getUsername());
            profile.put("email", updatedUser.getEmail());
            profile.put("createdAt", updatedUser.getCreatedAt());

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh-CN") String acceptLanguage) {
        try {
            userService.sendPasswordResetCode(request.getEmail(), acceptLanguage);

            Locale locale = messageService.parseLocale(acceptLanguage);
            Map<String, String> response = new HashMap<>();
            response.put("message", messageService.getMessage("api.success.code.sent", locale));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
            @RequestHeader(value = "Accept-Language", defaultValue = "zh-CN") String acceptLanguage) {
        try {
            userService.resetPassword(
                    request.getEmail(),
                    request.getVerificationCode(),
                    request.getNewPassword(),
                    acceptLanguage
            );

            Locale locale = messageService.parseLocale(acceptLanguage);
            Map<String, String> response = new HashMap<>();
            response.put("message", messageService.getMessage("api.success.password.reset", locale));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
