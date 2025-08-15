package com.vps.vpsserver.service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vps.vpsserver.entity.PasswordResetToken;
import com.vps.vpsserver.entity.User;
import com.vps.vpsserver.repository.PasswordResetTokenRepository;
import com.vps.vpsserver.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final MessageService messageService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    }

    public User registerUser(String username, String email, String password, String acceptLanguage) {
        Locale locale = messageService.parseLocale(acceptLanguage);
        
        // 验证输入参数
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException(messageService.getMessage("error.username.empty", locale));
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException(messageService.getMessage("error.email.empty", locale));
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException(messageService.getMessage("error.password.empty", locale));
        }
        
        String trimmedUsername = username.trim();
        String trimmedEmail = email.trim();
        
        if (userRepository.existsByUsername(trimmedUsername)) {
            throw new RuntimeException(messageService.getMessage("error.username.exists", locale));
        }
        if (userRepository.existsByEmail(trimmedEmail)) {
            throw new RuntimeException(messageService.getMessage("error.email.exists", locale));
        }

        User user = new User();
        user.setUsername(trimmedUsername);
        user.setEmail(trimmedEmail);
        user.setPassword(passwordEncoder.encode(password));

        log.info(user.getPassword());
        return userRepository.save(user);
    }

    public boolean authenticateUser(String username, String password, String acceptLanguage) {
        Locale locale = messageService.parseLocale(acceptLanguage);
        
        // 验证输入参数
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            throw new RuntimeException(messageService.getMessage("error.username.password.empty", locale));
        }
        
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new RuntimeException(messageService.getMessage("error.user.not.found", locale)));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException(messageService.getMessage("error.invalid.credentials", locale));
        }
        
        return true;
    }

    public User updateUserProfile(String currentUsername, String newUsername, String newEmail, 
                                String currentPassword, String newPassword, String acceptLanguage) {
        Locale locale = messageService.parseLocale(acceptLanguage);
        
        // 验证输入参数
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new RuntimeException(messageService.getMessage("error.username.empty", locale));
        }
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new RuntimeException(messageService.getMessage("error.email.empty", locale));
        }
        
        String trimmedNewUsername = newUsername.trim();
        String trimmedNewEmail = newEmail.trim();
        
        // 获取当前用户
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException(messageService.getMessage("error.user.not.found", locale)));
        
        // 检查用户名是否被其他用户占用
        if (!trimmedNewUsername.equals(currentUser.getUsername()) && 
            userRepository.existsByUsername(trimmedNewUsername)) {
            throw new RuntimeException(messageService.getMessage("error.username.exists", locale));
        }
        
        // 检查邮箱是否被其他用户占用
        if (!trimmedNewEmail.equals(currentUser.getEmail()) && 
            userRepository.existsByEmail(trimmedNewEmail)) {
            throw new RuntimeException(messageService.getMessage("error.email.exists", locale));
        }
        
        // 如果要修改密码
        if (currentPassword != null && !currentPassword.trim().isEmpty() && 
            newPassword != null && !newPassword.trim().isEmpty()) {
            
            // 验证当前密码
            if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
                throw new RuntimeException(messageService.getMessage("error.current.password.incorrect", locale));
            }
            
            // 设置新密码
            currentUser.setPassword(passwordEncoder.encode(newPassword));
        }
        
        // 更新用户信息
        currentUser.setUsername(trimmedNewUsername);
        currentUser.setEmail(trimmedNewEmail);
        
        return userRepository.save(currentUser);
    }

    public void sendPasswordResetCode(String email, String acceptLanguage) {
        Locale locale = messageService.parseLocale(acceptLanguage);
        
        // 验证邮箱是否存在并获取用户信息
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new RuntimeException(messageService.getMessage("error.email.not.registered", locale)));
        
        // 生成6位数字验证码
        String verificationCode = generateVerificationCode();
        
        // 删除该邮箱之前的所有重置令牌
        passwordResetTokenRepository.deleteByEmail(email.trim());
        
        // 创建新的重置令牌（10分钟有效期）
        PasswordResetToken resetToken = new PasswordResetToken(
            email.trim(),
            verificationCode,
            LocalDateTime.now().plusMinutes(10)
        );
        
        passwordResetTokenRepository.save(resetToken);
        
        // 发送邮件，包含用户名
        emailService.sendPasswordResetCode(email.trim(), user.getUsername(), verificationCode, locale);
    }

    public void resetPassword(String email, String verificationCode, String newPassword, String acceptLanguage) {
        Locale locale = messageService.parseLocale(acceptLanguage);
        
        // 验证输入参数
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException(messageService.getMessage("error.email.empty", locale));
        }
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            throw new RuntimeException(messageService.getMessage("error.verification.code.empty", locale));
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException(messageService.getMessage("error.password.empty", locale));
        }
        
        String trimmedEmail = email.trim();
        String trimmedCode = verificationCode.trim();
        
        // 查找有效的重置令牌
        PasswordResetToken resetToken = passwordResetTokenRepository
            .findByEmailAndTokenAndUsedFalse(trimmedEmail, trimmedCode)
            .orElseThrow(() -> new RuntimeException(messageService.getMessage("error.verification.code.invalid", locale)));
        
        // 检查令牌是否过期
        if (resetToken.isExpired()) {
            throw new RuntimeException(messageService.getMessage("error.verification.code.expired", locale));
        }
        
        // 查找用户
        User user = userRepository.findByEmail(trimmedEmail)
            .orElseThrow(() -> new RuntimeException(messageService.getMessage("error.user.not.found", locale)));
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // 标记令牌为已使用
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        
        // 删除该邮箱的所有重置令牌
        passwordResetTokenRepository.deleteByEmail(trimmedEmail);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
    

}