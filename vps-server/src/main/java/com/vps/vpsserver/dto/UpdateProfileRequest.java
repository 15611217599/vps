package com.vps.vpsserver.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    private String currentPassword;
    
    @Size(min = 8, max = 50, message = "新密码长度必须在8-50个字符之间")
    private String newPassword;
}