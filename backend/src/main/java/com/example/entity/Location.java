package com.example.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 区域实体类
 */
public class Location {
    // 主键ID
    private Integer id;
    // 区域名称
    private String name;
    // 总车位数
    private Integer total;
    // 空闲车位
    private Integer num;

    // 地址信息（表字段：address）
    private String address;
    // 地址组成信息（JSON 字符串，对应表字段：address_component）
    private String addressComponent;
    // 省 / 市 / 区
    private String province;
    private String city;
    private String district;
    // 经度 / 纬度（DECIMAL(10,7) 建议用 BigDecimal）
    private BigDecimal longitude;
    private BigDecimal latitude;
    // 计费规则
    private String pricingRules;

    // 停车场列表（非表字段）
    private List<ParkingLot> parkingLots;

    // ===== Getter / Setter =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }

    public Integer getNum() { return num; }
    public void setNum(Integer num) { this.num = num; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAddressComponent() { return addressComponent; }
    public void setAddressComponent(String addressComponent) { this.addressComponent = addressComponent; }


    public String getPricingRules() { return pricingRules; }
    public void setPricingRules(String pricingRules) { this.pricingRules = pricingRules; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public List<ParkingLot> getParkingLots() { return parkingLots; }
    public void setParkingLots(List<ParkingLot> parkingLots) { this.parkingLots = parkingLots; }
}