package com.vps.vpsserver.service;

import java.time.LocalDateTime;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户未找到: " + username));
    }

    public User registerUser(String username, String email, String password) {
        // 验证输入参数
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        
        String trimmedUsername = username.trim();
        String trimmedEmail = email.trim();
        
        if (userRepository.existsByUsername(trimmedUsername)) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.existsByEmail(trimmedEmail)) {
            throw new RuntimeException("邮箱已存在");
        }

        User user = new User();
        user.setUsername(trimmedUsername);
        user.setEmail(trimmedEmail);
        user.setPassword(passwordEncoder.encode(password));

        log.info(user.getPassword());
        return userRepository.save(user);
    }

    public boolean authenticateUser(String username, String password) {
        // 验证输入参数
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            throw new RuntimeException("用户名和密码不能为空");
        }
        
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码不正确");
        }
        
        return true;
    }

    public User updateUserProfile(String currentUsername, String newUsername, String newEmail, 
                                String currentPassword, String newPassword) {
        // 验证输入参数
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }
        
        String trimmedNewUsername = newUsername.trim();
        String trimmedNewEmail = newEmail.trim();
        
        // 获取当前用户
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        
        // 检查用户名是否被其他用户占用
        if (!trimmedNewUsername.equals(currentUser.getUsername()) && 
            userRepository.existsByUsername(trimmedNewUsername)) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否被其他用户占用
        if (!trimmedNewEmail.equals(currentUser.getEmail()) && 
            userRepository.existsByEmail(trimmedNewEmail)) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 如果要修改密码
        if (currentPassword != null && !currentPassword.trim().isEmpty() && 
            newPassword != null && !newPassword.trim().isEmpty()) {
            
            // 验证当前密码
            if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
                throw new RuntimeException("当前密码不正确");
            }
            
            // 设置新密码
            currentUser.setPassword(passwordEncoder.encode(newPassword));
        }
        
        // 更新用户信息
        currentUser.setUsername(trimmedNewUsername);
        currentUser.setEmail(trimmedNewEmail);
        
        return userRepository.save(currentUser);
    }

    public void sendPasswordResetCode(String email) {
        // 验证邮箱是否存在并获取用户信息
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new RuntimeException("该邮箱未注册"));
        
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
        emailService.sendPasswordResetCode(email.trim(), user.getUsername(), verificationCode);
    }

    public void resetPassword(String email, String verificationCode, String newPassword) {
        // 验证输入参数
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            throw new RuntimeException("验证码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        
        String trimmedEmail = email.trim();
        String trimmedCode = verificationCode.trim();
        
        // 查找有效的重置令牌
        PasswordResetToken resetToken = passwordResetTokenRepository
            .findByEmailAndTokenAndUsedFalse(trimmedEmail, trimmedCode)
            .orElseThrow(() -> new RuntimeException("验证码无效"));
        
        // 检查令牌是否过期
        if (resetToken.isExpired()) {
            throw new RuntimeException("验证码已过期");
        }
        
        // 查找用户
        User user = userRepository.findByEmail(trimmedEmail)
            .orElseThrow(() -> new RuntimeException("用户未找到"));
        
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