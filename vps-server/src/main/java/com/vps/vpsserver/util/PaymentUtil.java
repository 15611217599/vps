package com.vps.vpsserver.util;

import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class PaymentUtil {

    /**
     * 生成MD5签名
     * @param params 参数Map
     * @param key 商户密钥
     * @return 签名字符串
     */
    public static String generateSign(Map<String, String> params, String key) {
        // 1. 按参数名ASCII码从小到大排序
        TreeMap<String, String> sortedParams = new TreeMap<>(params);
        
        // 2. 拼接参数，排除sign、sign_type和空值
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            String paramKey = entry.getKey();
            String paramValue = entry.getValue();
            
            if (!"sign".equals(paramKey) && !"sign_type".equals(paramKey) 
                && paramValue != null && !paramValue.trim().isEmpty()) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(paramKey).append("=").append(paramValue);
            }
        }
        
        // 3. 拼接商户密钥
        sb.append(key);
        
        // 4. MD5加密
        return md5(sb.toString()).toLowerCase();
    }

    /**
     * 验证签名
     * @param params 参数Map
     * @param key 商户密钥
     * @param sign 待验证的签名
     * @return 验证结果
     */
    public static boolean verifySign(Map<String, String> params, String key, String sign) {
        String generatedSign = generateSign(params, key);
        return generatedSign.equals(sign);
    }

    /**
     * MD5加密
     * @param input 输入字符串
     * @return MD5值
     */
    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
}