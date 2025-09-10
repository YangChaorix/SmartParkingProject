package com.example.entity;


/**
 * 车辆信息实体类
*/
public class Vehicle {
    // 主键ID
    private Integer id;
    // 车牌号
    private String name;
    // 所属用户Id
    private Integer userId;
    // 用户名称
    private String userName;
    // 创建时间
    private String createdAt;

    // 获取主键ID
    public Integer getId() {
        return id;
    }

    // 设置主键ID
    public void setId(Integer id) {
        this.id = id;
    }

    // 获取车牌号
    public String getName() {
        return name;
    }

    // 设置车牌号
    public void setName(String name) {
        this.name = name;
    }

    // 获取所属用户Id
    public Integer getUserId() {
        return userId;
    }

    // 设置所属用户Id
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // 获取用户名称
    public String getUserName() {
        return userName;
    }

    // 设置用户名称
    public void setUserName(String userName) {
        this.userName = userName;
    }

    // 获取创建时间
    public String getCreatedAt() {
        return createdAt;
    }

    // 设置创建时间
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}