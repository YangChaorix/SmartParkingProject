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
import com.example.exception.CustomException;
import com.example.mapper.ParkingMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        pay.setPrice(0.1 * pay.getMinutes());
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

}