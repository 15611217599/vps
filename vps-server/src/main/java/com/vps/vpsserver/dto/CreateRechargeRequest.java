package com.vps.vpsserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRechargeRequest {
    private BigDecimal amount;
    private String paymentMethod; // 支付方式：ALIPAY, WECHAT, BANK_CARD等
    private String description;   // 充值说明
}
