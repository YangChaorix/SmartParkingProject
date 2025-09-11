package com.example.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DateTimeUtils测试类
 */
public class DateTimeUtilsTest {
    
    @Test
    public void testGetCurrentDateTime() {
        // 测试获取当前时间
        String currentTime = DateTimeUtils.getCurrentDateTime();
        
        // 验证时间格式
        assertNotNull(currentTime);
        assertTrue(currentTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        
        System.out.println("当前时间: " + currentTime);
    }
    
    @Test
    public void testGetCurrentDateTimeWithPattern() {
        // 测试自定义格式
        String customTime = DateTimeUtils.getCurrentDateTime("yyyy年MM月dd日 HH时mm分ss秒");
        
        assertNotNull(customTime);
        assertTrue(customTime.contains("年") && customTime.contains("月") && customTime.contains("日"));
        
        System.out.println("自定义格式时间: " + customTime);
    }
    
    @Test
    public void testFormatDateTime() {
        // 测试格式化LocalDateTime
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String formattedTime = DateTimeUtils.formatDateTime(now);
        
        assertNotNull(formattedTime);
        assertTrue(formattedTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        
        System.out.println("格式化时间: " + formattedTime);
    }
}



