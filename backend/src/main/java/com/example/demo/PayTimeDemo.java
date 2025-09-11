package com.example.demo;

import com.example.entity.Pay;
import com.example.utils.DateTimeUtils;

/**
 * 支付时间功能演示程序
 */
public class PayTimeDemo {
    
    public static void main(String[] args) {
        System.out.println("=== 支付时间功能演示 ===");
        
        // 模拟创建订单
        Pay pay = new Pay();
        pay.setId(1);
        pay.setUserId(100);
        pay.setVehicleId("V001");
        pay.setLocationId(1);
        pay.setParkingLotId(1);
        pay.setStartTime("2025-01-09 20:00:00");
        pay.setEndTime("2025-01-09 22:00:00");
        pay.setMinutes(120);
        pay.setPrice(10.0);
        pay.setSerialNumber("2025010922000012345");
        pay.setStatus("未缴费");
        
        System.out.println("订单创建时:");
        System.out.println("订单编号: " + pay.getSerialNumber());
        System.out.println("支付状态: " + pay.getStatus());
        System.out.println("支付时间: " + (pay.getPayTime() != null ? pay.getPayTime() : "未支付"));
        
        System.out.println("\n模拟支付过程...");
        
        // 模拟支付过程
        pay.setStatus("已缴费");
        pay.setPayTime(DateTimeUtils.getCurrentDateTime());
        
        System.out.println("\n支付完成后:");
        System.out.println("订单编号: " + pay.getSerialNumber());
        System.out.println("支付状态: " + pay.getStatus());
        System.out.println("支付时间: " + pay.getPayTime());
        
        System.out.println("\n=== 时间格式说明 ===");
        System.out.println("支付时间格式: yyyy-MM-dd HH:mm:ss");
        System.out.println("示例: 2025-01-09 22:30:45");
        System.out.println("说明: 年-月-日 时:分:秒");
    }
}



