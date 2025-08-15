package com.vps.vpsserver.service;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageSource messageSource;
    
    /**
     * 获取国际化消息
     * 
     * @param key 消息键
     * @param locale 语言环境
     * @return 国际化消息
     */
    public String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
    
    /**
     * 获取带参数的国际化消息
     * 
     * @param key 消息键
     * @param args 参数
     * @param locale 语言环境
     * @return 国际化消息
     */
    public String getMessage(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }
    
    /**
     * 解析语言环境
     * 
     * @param acceptLanguage Accept-Language头
     * @return Locale对象
     */
    public Locale parseLocale(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.trim().isEmpty()) {
            return Locale.SIMPLIFIED_CHINESE;
        }
        
        // 解析Accept-Language头，取第一个语言
        String[] languages = acceptLanguage.split(",");
        String primaryLanguage = languages[0].trim();
        
        if (primaryLanguage.startsWith("en")) {
            return Locale.US;
        } else if (primaryLanguage.startsWith("zh")) {
            return Locale.SIMPLIFIED_CHINESE;
        } else {
            return Locale.SIMPLIFIED_CHINESE; // 默认中文
        }
    }
}