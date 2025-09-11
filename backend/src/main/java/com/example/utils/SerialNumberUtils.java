package com.example.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单编号生成工具类
 * 生成格式：年月日时分秒+五位随机数，例如：20250909223045123456
 */
public class SerialNumberUtils {
    
    private static final Random random = new Random();
    
    /**
     * 生成唯一订单编号
     * 格式：年月日时分秒+五位随机数
     * 例如：20250909223045123456
     * 
     * @return 唯一订单编号
     */
    public static String generateSerialNumber() {
        // 获取当前时间并格式化为年月日时分秒
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timeString = now.format(formatter);
        
        // 生成五位随机数
        int randomNumber = random.nextInt(100000); // 生成0-99999的随机数
        String randomString = String.format("%05d", randomNumber); // 格式化为5位数字，不足补0
        
        // 拼接时间字符串和随机数
        return timeString + randomString;
    }
    
    /**
     * 生成指定前缀的订单编号
     * 格式：前缀+年月日时分秒+五位随机数
     * 例如：PAY20250909223045123456
     * 
     * @param prefix 前缀
     * @return 带前缀的唯一订单编号
     */
    public static String generateSerialNumber(String prefix) {
        return prefix + generateSerialNumber();
    }
}



