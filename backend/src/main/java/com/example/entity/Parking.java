package com.example.entity;

/**
 * 停车信息实体类
 */
public class Parking {
    // 主键ID
    private Integer id;
    // 用户Id
    private Integer userId;
    // 车辆Id
    private String vehicleId;
    // 区域Id
    private Integer locationId;
    // 车位Id
    private Integer parkingLotId;
    // 入场时间
    private String startTime;
    // 出场时间
    private String endTime;
    // 状态
    private String status;
    // 用户名称
    private String userName;
    // 车辆名称
    private String vehicleName;
    // 区域名称
    private String locationName;
    // 车位名称
    private String parkingLotName;

    // 获取主键ID
    public Integer getId() {
        return id;
    }

    // 设置主键ID
    public void setId(Integer id) {
        this.id = id;
    }

    // 获取用户Id
    public Integer getUserId() {
        return userId;
    }

    // 设置用户Id
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

    // 获取车辆Id
    public String getVehicleId() {
        return vehicleId;
    }

    // 设置车辆Id
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    // 获取车辆名称
    public String getVehicleName() {
        return vehicleName;
    }

    // 设置车辆名称
    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
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

    // 获取车位Id
    public Integer getParkingLotId() {
        return parkingLotId;
    }

    // 设置车位Id
    public void setParkingLotId(Integer parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    // 获取车位名称
    public String getParkingLotName() {
        return parkingLotName;
    }

    // 设置车位名称
    public void setParkingLotName(String parkingLotName) {
        this.parkingLotName = parkingLotName;
    }

    // 获取入场时间
    public String getStartTime() {
        return startTime;
    }

    // 设置入场时间
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    // 获取出场时间
    public String getEndTime() {
        return endTime;
    }

    // 设置出场时间
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    // 获取状态
    public String getStatus() {
        return status;
    }

    // 设置状态
    public void setStatus(String status) {
        this.status = status;
    }
}