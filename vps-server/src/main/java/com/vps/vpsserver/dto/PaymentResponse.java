package com.vps.vpsserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Integer code; // 返回状态码：1为成功，其它值为失败
    private String msg; // 返回信息
    private String tradeNo; // 订单号
    private String payUrl; // 支付跳转URL
    private String qrCode; // 支付二维码链接
    private String urlScheme; // 小程序跳转URL
    private String outTradeNo; // 商户订单号
}