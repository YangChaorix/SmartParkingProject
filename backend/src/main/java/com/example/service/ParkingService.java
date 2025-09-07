package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Parking;
import com.example.entity.ParkingLot;
import com.example.entity.Pay;
import com.example.entity.Location;
import com.example.exception.CustomException;
import com.example.mapper.ParkingMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.core.util.StrUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 停车信息业务处理类，负责停车信息的增删改查等操作。
 **/
@Service
public class ParkingService {

    @Resource
    private ParkingMapper parkingMapper; // 注入ParkingMapper用于数据库操作

    @Resource
    private ParkingLotService parkingLotService; // 注入ParkingLotService用于操作停车位信息

    @Resource
    private PayService payService; // 注入PayService用于操作缴费信息

    @Resource
    private LocationService locationService; // 注入LocationService用于获取区域信息

    /**
     * 添加停车信息。
     * @param parking 停车实体
     */
    public void add(Parking parking) {
        // 查询当前用户当前车辆是否已经在停车场了
        Parking parking1 = new Parking();
        parking1.setUserId(parking.getUserId());
        parking1.setVehicleId(parking.getVehicleId());
        parking1.setStatus("已入场");
        List<Parking> parkings = parkingMapper.selectAll(parking1);
        if (CollectionUtil.isNotEmpty(parkings)) {
            throw new CustomException("500", "当前车辆还未驶出停车场");
        }
        // 查询当前用户当前车辆有没有未交费的记录
        Pay pay = new Pay();
        pay.setStatus("未缴费");
        pay.setUserId(parking.getUserId());
        pay.setVehicleId(parking.getVehicleId());
        List<Pay> pays = payService.selectAll(pay);
        if (CollectionUtil.isNotEmpty(pays)) {
            throw new CustomException("500", "当前车辆还有未缴费的停车记录，禁止入场");
        }
        parking.setStatus("已入场");
        parkingMapper.insert(parking);
        // 更新停车位状态
        ParkingLot parkingLot = parkingLotService.selectById(parking.getParkingLotId());
        if (ObjUtil.isNotEmpty(parkingLot)) {
            parkingLot.setStatus("占用");
            parkingLotService.updateById(parkingLot);
        }
    }

    /**
     * 根据ID删除停车信息。
     * @param id 停车信息ID
     */
    public void deleteById(Integer id) {
        parkingMapper.deleteById(id); // 删除操作
    }

    /**
     * 批量删除停车信息。
     * @param ids 停车信息ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            parkingMapper.deleteById(id); // 批量删除操作
        }
    }

    /**
     * 更新停车信息。
     * @param parking 停车实体
     */
    public void updateById(Parking parking) {
        if (ObjUtil.isEmpty(parking.getEndTime())) {
            throw new CustomException("500", "请选择出场时间");
        }
        parking.setStatus("已出场");
        parkingMapper.updateById(parking);
        // 初始化缴费
        Pay pay = new Pay();
        BeanUtils.copyProperties(parking, pay);
        Long minutes = DateUtil.between(DateUtil.parse(pay.getStartTime()), DateUtil.parse(pay.getEndTime()), DateUnit.MINUTE);
        pay.setMinutes(minutes.intValue());
        // 计算缴费
        String pricingRulesJson = null;
        if (parking.getParkingLotId() != null) {
            ParkingLot parkingLot = parkingLotService.selectById(parking.getParkingLotId());
            if (ObjUtil.isNotEmpty(parkingLot) && parkingLot.getLocationId() != null) {
                Location location = locationService.selectById(parkingLot.getLocationId());
                if (ObjUtil.isNotEmpty(location)) {
                    pricingRulesJson = location.getPricingRules();
                }
            }
        }
        // 调用 calculatePrice 方法计算费用
        double finalPrice = calculatePrice(minutes, pricingRulesJson);
        System.out.println(finalPrice);
        pay.setPrice(finalPrice);
//        pay.setPrice(0.1 * pay.getMinutes());
        pay.setStatus("未缴费");
        payService.add(pay);
        // 更新停车位状态
        ParkingLot parkingLot = parkingLotService.selectById(parking.getParkingLotId());
        if (ObjUtil.isNotEmpty(parkingLot)) {
            parkingLot.setStatus("空闲");
            parkingLotService.updateById(parkingLot);
        }
    }

    /**
     * 根据ID查询停车信息。
     * @param id 停车信息ID
     * @return 停车实体
     */
    public Parking selectById(Integer id) {
        return parkingMapper.selectById(id); // 查询操作
    }

    /**
     * 查询所有停车信息。
     * @param parking 停车实体（可选条件）
     * @return 停车列表
     */
    public List<Parking> selectAll(Parking parking) {
        return parkingMapper.selectAll(parking); // 查询所有操作
    }

    /**
     * 分页查询停车信息。
     * @param parking 停车实体（可选条件）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageInfo<Parking> selectPage(Parking parking, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            parking.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize); // 开始分页
        List<Parking> list = this.selectAll(parking); // 查询所有操作
        return PageInfo.of(list); // 返回分页结果
    }

    /**
     * 计算停车费用
     * @param minutes 停车时长（分钟）
     * @param pricingRulesJson 计费规则的JSON字符串
     * @return 停车费用
     */
    private double calculatePrice(Long minutes, String pricingRulesJson) {
        // 1. 定义默认的计费规则值
        int freeMinutes = 30;
        int firstTierMinutes = 120;
        double firstTierPrice = 0.08;
        double secondTierPrice = 0.05;
        double dailyCap = 50.0;

        // 2. 尝试解析JSON字符串并覆盖默认值
        if (StrUtil.isNotEmpty(pricingRulesJson) && JSONUtil.isJson(pricingRulesJson)) {
            try {
                JSONObject jsonObject = JSONUtil.parseObj(pricingRulesJson);

                // 使用getXXX方法安全地获取值，如果JSON中没有该键，则使用默认值
                freeMinutes = jsonObject.getInt("freeMinutes", freeMinutes);
                firstTierMinutes = jsonObject.getInt("firstTierMinutes", firstTierMinutes);
                firstTierPrice = jsonObject.getDouble("firstTierPrice", firstTierPrice);
                secondTierPrice = jsonObject.getDouble("secondTierPrice", secondTierPrice);
                dailyCap = jsonObject.getDouble("dailyCap", dailyCap);

            } catch (Exception e) {
                // 如果解析失败，则抛出异常，阻止错误的费用计算
                throw new CustomException("500", "计费规则数据格式不正确，请联系管理员");
            }
        }

        // 3. 根据解析或默认的规则进行分段计算
        double finalPrice = 0.0;

        // 免费停车
        if (minutes <= freeMinutes) {
            return 0.00;
        }

        long remainingMinutes = minutes - freeMinutes;

        // 每日封顶逻辑（简化处理，只考虑整数天）
        long days = remainingMinutes / (24 * 60);
        remainingMinutes = remainingMinutes % (24 * 60);

        double currentDayPrice = 0.0;
        // 首段计费
        if (remainingMinutes <= firstTierMinutes) {
            currentDayPrice = remainingMinutes * firstTierPrice;
        }
        // 后续计费
        else {
            currentDayPrice = firstTierMinutes * firstTierPrice;
            currentDayPrice += (remainingMinutes - firstTierMinutes) * secondTierPrice;
        }

        // 单日封顶逻辑
        if (currentDayPrice > dailyCap) {
            currentDayPrice = dailyCap;
        }

        finalPrice = days * dailyCap + currentDayPrice;

        return new BigDecimal(finalPrice).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}