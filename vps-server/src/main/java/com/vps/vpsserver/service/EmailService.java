package com.vps.vpsserver.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final MessageSource messageSource;
    private final MessageService messageService;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * 发送密码重置验证码邮件
     * 
     * @param email 收件人邮箱
     * @param username 用户名
     * @param verificationCode 验证码
     * @param locale 语言环境
     */
    public void sendPasswordResetCode(String email, String username, String verificationCode, Locale locale) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            String systemName = messageSource.getMessage("email.system.name", null, locale);
            String subject = messageSource.getMessage("email.password.reset.subject", null, locale);
            
            helper.setFrom(fromEmail, systemName);
            helper.setTo(email);
            helper.setSubject(subject);
            
            String htmlContent = buildPasswordResetEmailContent(username, verificationCode, locale);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("密码重置验证码已发送到: {} (用户: {}, 语言: {})", email, username, locale.getLanguage());
            
        } catch (MessagingException e) {
            log.error("发送邮件失败: {}", e.getMessage(), e);
            throw new RuntimeException(messageService.getMessage("error.email.send.failed", locale));
        } catch (Exception e) {
            log.error("邮件服务异常: {}", e.getMessage(), e);
            throw new RuntimeException(messageService.getMessage("error.email.service.unavailable", locale));
        }
    }
    
    /**
     * 构建密码重置邮件内容
     */
    private String buildPasswordResetEmailContent(String username, String verificationCode, Locale locale) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // 获取国际化消息
        String systemName = messageSource.getMessage("email.system.name", null, locale);
        String subject = messageSource.getMessage("email.password.reset.subject", null, locale);
        String greeting = messageSource.getMessage("email.password.reset.greeting", new Object[]{username}, locale);
        String content = messageSource.getMessage("email.password.reset.content", null, locale);
        String codeInstruction = messageSource.getMessage("email.password.reset.code.instruction", null, locale);
        String securityTitle = messageSource.getMessage("email.password.reset.security.title", null, locale);
        String securityTip1 = messageSource.getMessage("email.password.reset.security.tip1", null, locale);
        String securityTip2 = messageSource.getMessage("email.password.reset.security.tip2", null, locale);
        String securityTip3 = messageSource.getMessage("email.password.reset.security.tip3", null, locale);
        String contact = messageSource.getMessage("email.password.reset.contact", null, locale);
        String wishes = messageSource.getMessage("email.password.reset.wishes", null, locale);
        String footerAuto = messageSource.getMessage("email.password.reset.footer.auto", null, locale);
        String footerTime = messageSource.getMessage("email.password.reset.footer.time", new Object[]{currentTime}, locale);
        
        return "<html>" +
                "<head><meta charset='UTF-8'><title>" + subject + "</title></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'>" +
                "<h1>🔐 " + systemName + "</h1>" +
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
                "<strong>⚠️ " + securityTitle + "</strong>" +
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