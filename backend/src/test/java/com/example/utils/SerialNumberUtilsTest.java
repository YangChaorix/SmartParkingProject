package com.example.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SerialNumberUtils测试类
 */
public class SerialNumberUtilsTest {
    
    @Test
    public void testGenerateSerialNumber() {
        // 测试生成订单编号
        String serialNumber = SerialNumberUtils.generateSerialNumber();
        
        // 验证编号不为空
        assertNotNull(serialNumber);
        
        // 验证编号长度（年月日时分秒14位 + 5位随机数 = 19位）
        assertEquals(19, serialNumber.length());
        
        // 验证编号格式（前14位应该是数字）
        assertTrue(serialNumber.matches("\\d{19}"));
        
        System.out.println("生成的订单编号: " + serialNumber);
    }
    
    @Test
    public void testGenerateSerialNumberWithPrefix() {
        // 测试生成带前缀的订单编号
        String prefix = "PAY";
        String serialNumber = SerialNumberUtils.generateSerialNumber(prefix);
        
        // 验证编号不为空
        assertNotNull(serialNumber);
        
        // 验证编号以指定前缀开头
        assertTrue(serialNumber.startsWith(prefix));
        
        // 验证编号长度（前缀长度 + 年月日时分秒14位 + 5位随机数）
        assertEquals(prefix.length() + 19, serialNumber.length());
        
        System.out.println("生成的带前缀订单编号: " + serialNumber);
    }
    
    @Test
    public void testSerialNumberUniqueness() {
        // 测试生成多个编号的唯一性
        String serialNumber1 = SerialNumberUtils.generateSerialNumber();
        String serialNumber2 = SerialNumberUtils.generateSerialNumber();
        
        // 验证两个编号不同（虽然理论上可能相同，但概率极低）
        assertNotEquals(serialNumber1, serialNumber2);
        
        System.out.println("编号1: " + serialNumber1);
        System.out.println("编号2: " + serialNumber2);
    }
}



