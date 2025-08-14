package com.example.entity;


/**
 * 车位信息
*/
public class ParkingLot {
    // 主键ID
    private Integer id;
    // 车位编号
    private String name;
    // 区域Id
    private Integer locationId;
    // 车位状态
    private String status;
    // 区域名称
    private String locationName;

    // 获取主键ID
    public Integer getId() {
        return id;
    }

    // 设置主键ID
    public void setId(Integer id) {
        this.id = id;
    }

    // 获取车位编号
    public String getName() {
        return name;
    }

    // 设置车位编号
    public void setName(String name) {
        this.name = name;
    }

    // 获取区域Id
    public Integer getLocationId() {
        return locationId;
    }

    // 设置区域Id
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    // 获取区域名称
    public String getLocationName() {
        return locationName;
    }

    // 设置区域名称
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    // 获取车位状态
    public String getStatus() {
        return status;
    }

    // 设置车位状态
    public void setStatus(String status) {
        this.status = status;
    }
}