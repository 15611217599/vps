package com.vps.vpsserver.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}