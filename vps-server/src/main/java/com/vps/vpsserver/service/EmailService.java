package com.vps.vpsserver.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * 发送密码重置验证码邮件（中文固定模板）
     *
     * @param email 收件人邮箱
     * @param username 用户名
     * @param verificationCode 验证码
     */
    public void sendPasswordResetCode(String email, String username, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            String systemName = "VPS 管理系统";
            String subject = "密码重置验证码";
            
            helper.setFrom(fromEmail, systemName);
            helper.setTo(email);
            helper.setSubject(subject);
            
            String htmlContent = buildPasswordResetEmailContent(username, verificationCode);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("密码重置验证码已发送到: {} (用户: {})", email, username);
            
        } catch (MessagingException e) {
            log.error("发送邮件失败: {}", e.getMessage(), e);
            throw new RuntimeException("发送邮件失败");
        } catch (Exception e) {
            log.error("邮件服务异常: {}", e.getMessage(), e);
            throw new RuntimeException("邮件服务不可用");
        }
    }
    
    /**
     * 构建密码重置邮件内容
     */
    private String buildPasswordResetEmailContent(String username, String verificationCode) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        String systemName = "VPS 管理系统";
        String subject = "密码重置验证码";
        String greeting = "您好，" + username + "：";
        String content = "我们收到了您重置密码的请求。以下是您的验证码：";
        String codeInstruction = "请在 10 分钟内使用该验证码完成密码重置。";
        String securityTitle = "安全提示";
        String securityTip1 = "请勿将验证码泄露给他人。";
        String securityTip2 = "如果不是您本人操作，请忽略此邮件。";
        String securityTip3 = "建议尽快修改密码以保障账户安全。";
        String contact = "如有疑问，请联系系统管理员。";
        String wishes = "祝您使用愉快！";
        String footerAuto = "此邮件由系统自动发送，请勿回复。";
        String footerTime = "发送时间：" + currentTime;
        
        return "<html>" +
                "<head><meta charset='UTF-8'><title>" + subject + "</title></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'>" +
                "<h1> " + systemName + "</h1>" +
                "<p>" + subject + "</p>" +
                "</div>" +
                "<div style='background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px;'>" +
                "<p>" + greeting + "</p>" +
                "<p>" + content + "</p>" +
                "<div style='background: #fff; border: 2px dashed #667eea; padding: 20px; text-align: center; margin: 20px 0; border-radius: 8px;'>" +
                "<div style='font-size: 32px; font-weight: bold; color: #667eea; letter-spacing: 5px;'>" + verificationCode + "</div>" +
                "<p style='margin: 10px 0 0 0; color: #666;'>" + codeInstruction + "</p>" +
                "</div>" +
                "<div style='background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                "<strong> " + securityTitle + "</strong>" +
                "<ul style='margin: 10px 0 0 0; padding-left: 20px;'>" +
                "<li>" + securityTip1 + "</li>" +
                "<li>" + securityTip2 + "</li>" +
                "<li>" + securityTip3 + "</li>" +
                "</ul>" +
                "</div>" +
                "<p>" + contact + "</p>" +
                "<p>" + wishes + "</p>" +
                "</div>" +
                "<div style='text-align: center; margin-top: 30px; color: #666; font-size: 12px;'>" +
                "<p>" + footerAuto + "</p>" +
                "<p>" + footerTime + "</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}