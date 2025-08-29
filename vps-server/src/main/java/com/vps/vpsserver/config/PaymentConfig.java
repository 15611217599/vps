package com.vps.vpsserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "payment.ezpay")
@Data
public class PaymentConfig {
    
    private String pid = "1001"; // 商户ID
    private String key = "89unJUB8HZ54Hj7x4nUj56HN4nUzUJ8i"; // 商户密钥
    private String submitUrl = "https://ezfp.cn/submit.php"; // 页面跳转支付URL
    private String apiUrl = "https://ezfp.cn/mapi.php"; // API接口支付URL
    private String notifyUrl = "http://localhost:8080/api/payment/notify"; // 异步通知地址
    private String returnUrl = "http://localhost:3000/payment/return"; // 跳转通知地址
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}