package com.vps.vpsserver.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private BigDecimal money; // 支付金额
    private String type; // 支付方式：alipay, wxpay, qqpay, bank, jdpay, paypal, usdt
    private String name = "账户充值"; // 商品名称
    private String device = "pc"; // 设备类型：pc, mobile, qq, wechat, alipay
    private String param; // 业务扩展参数
}