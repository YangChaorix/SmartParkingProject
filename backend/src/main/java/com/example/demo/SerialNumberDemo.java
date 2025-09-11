package com.example.demo;

import com.example.utils.SerialNumberUtils;

/**
 * 订单编号生成演示程序
 */
public class SerialNumberDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 订单编号生成演示 ===");
        
        // 生成10个订单编号进行演示
        for (int i = 1; i <= 10; i++) {
            String serialNumber = SerialNumberUtils.generateSerialNumber();
            System.out.println("订单编号 " + i + ": " + serialNumber);
        }
        
        System.out.println("\n=== 带前缀的订单编号生成演示 ===");
        
        // 生成带前缀的订单编号
        String[] prefixes = {"PAY", "ORDER", "PARK"};
        for (String prefix : prefixes) {
            String serialNumber = SerialNumberUtils.generateSerialNumber(prefix);
            System.out.println(prefix + " 订单编号: " + serialNumber);
        }
        
        System.out.println("\n=== 编号格式说明 ===");
        System.out.println("格式: 年月日时分秒(14位) + 五位随机数(5位) = 19位");
        System.out.println("示例: 20250909223045123456");
        System.out.println("      |--时间部分--| |随机数|");
    }
}



