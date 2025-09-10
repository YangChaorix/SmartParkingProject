package com.example.entity;


/**
 * 停车信息实体类
*/
public class Pay {
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
    // 用户名称
    private String userName;
    // 车辆名称
    private String vehicleName;
    // 区域名称
    private String locationName;
    // 车位名称
    private String parkingLotName;
    // 停车时长（分钟）
    private Integer minutes;
    // 停车费用
    private Double price;
    // 订单编号
    private String serialNumber;
    // 支付时间
    private String payTime;
    // 支付状态
    private String status;

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

    // 获取停车时长（分钟）
    public Integer getMinutes() {
        return minutes;
    }

    // 设置停车时长（分钟）
    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    // 获取停车费用
    public Double getPrice() {
        return price;
    }

    // 设置停车费用
    public void setPrice(Double price) {
        this.price = price;
    }

    // 获取订单编号
    public String getSerialNumber() {
        return serialNumber;
    }

    // 设置订单编号
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    // 获取支付时间
    public String getPayTime() {
        return payTime;
    }

    // 设置支付时间
    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    // 获取支付状态
    public String getStatus() {
        return status;
    }

    // 设置支付状态
    public void setStatus(String status) {
        this.status = status;
    }
}