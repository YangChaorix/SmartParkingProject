package com.example.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 * 提供统一的时间格式化方法
 */
public class DateTimeUtils {
    
    /**
     * 标准日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String STANDARD_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 获取当前时间的标准格式字符串
     * 格式：yyyy-MM-dd HH:mm:ss
     * 例如：2025-01-09 22:30:45
     * 
     * @return 当前时间的标准格式字符串
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(STANDARD_DATETIME_FORMAT));
    }
    
    /**
     * 获取当前时间的指定格式字符串
     * 
     * @param pattern 时间格式模式
     * @return 当前时间的指定格式字符串
     */
    public static String getCurrentDateTime(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 格式化LocalDateTime为标准格式字符串
     * 
     * @param dateTime LocalDateTime对象
     * @return 标准格式的时间字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(STANDARD_DATETIME_FORMAT));
    }
    
    /**
     * 格式化LocalDateTime为指定格式字符串
     * 
     * @param dateTime LocalDateTime对象
     * @param pattern 时间格式模式
     * @return 指定格式的时间字符串
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}


