package com.vps.vpsserver.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 国际化服务
 */
@Service
public class I18nService {
    
    @Autowired
    private MessageSource messageSource;
    
    /**
     * 从请求头获取当前语言
     */
    public String getCurrentLanguage() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String acceptLanguage = request.getHeader("Accept-Language");
            
            if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
                // 处理多种格式：en, en-US, zh, zh-CN, zh-TW等
                String language = acceptLanguage.toLowerCase().trim();
                
                // 取第一个语言（如果有多个用逗号分隔）
                if (language.contains(",")) {
                    language = language.split(",")[0].trim();
                }
                
                if (language.startsWith("en")) {
                    return "en";
                } else if (language.startsWith("zh")) {
                    return "zh";
                }
            }
        }
        return "zh"; // 默认中文
    }
    
    /**
     * 获取当前语言的Locale
     */
    public Locale getCurrentLocale() {
        String language = getCurrentLanguage();
        if ("en".equals(language)) {
            return Locale.US;
        } else {
            return Locale.SIMPLIFIED_CHINESE;
        }
    }
    
    /**
     * 获取国际化消息
     */
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, getCurrentLocale());
    }
    
    /**
     * 获取国际化消息，带默认值
     */
    public String getMessage(String key, String defaultMessage, Object... args) {
        return messageSource.getMessage(key, args, defaultMessage, getCurrentLocale());
    }
    
    /**
     * 判断是否为英文环境
     */
    public boolean isEnglish() {
        return "en".equals(getCurrentLanguage());
    }
    
    /**
     * 判断是否为中文环境
     */
    public boolean isChinese() {
        return "zh".equals(getCurrentLanguage());
    }
}