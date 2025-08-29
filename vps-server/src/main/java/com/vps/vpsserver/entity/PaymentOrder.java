package com.vps.vpsserver.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "out_trade_no", unique = true, nullable = false)
    private String outTradeNo; // 商户订单号

    @Column(name = "trade_no")
    private String tradeNo; // 易支付订单号

    @Column(name = "api_trade_no")
    private String apiTradeNo; // 第三方订单号

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type; // 支付方式

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal money; // 支付金额

    @Column(nullable = false)
    private String name; // 商品名称

    @Column(name = "client_ip")
    private String clientIp; // 用户IP

    @Column(name = "device")
    private String device = "pc"; // 设备类型

    @Column(name = "param")
    private String param; // 业务扩展参数

    @Column(name = "pay_url")
    private String payUrl; // 支付跳转URL

    @Column(name = "qr_code")
    private String qrCode; // 支付二维码

    @Column(name = "url_scheme")
    private String urlScheme; // 小程序跳转URL

    @Column(name = "buyer")
    private String buyer; // 支付者账号

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (outTradeNo == null) {
            outTradeNo = generateOutTradeNo();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == PaymentStatus.SUCCESS && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }

    private String generateOutTradeNo() {
        return System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }

    public enum PaymentType {
        ALIPAY("alipay", "支付宝"),
        WXPAY("wxpay", "微信支付"),
        QQPAY("qqpay", "QQ钱包"),
        BANK("bank", "网银支付"),
        JDPAY("jdpay", "京东支付"),
        PAYPAL("paypal", "PayPal"),
        USDT("usdt", "泰达币");

        private final String code;
        private final String name;

        PaymentType(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    public enum PaymentStatus {
        PENDING,    // 待支付
        SUCCESS,    // 支付成功
        FAILED,     // 支付失败
        CANCELLED   // 已取消
    }
}