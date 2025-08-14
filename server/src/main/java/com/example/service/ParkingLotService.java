package com.example.service;

import cn.hutool.core.util.ObjUtil;
import com.example.entity.Location;
import com.example.entity.ParkingLot;
import com.example.mapper.ParkingLotMapper;
import com.example.service.LocationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 车位信息业务处理类，负责车位信息的增删改查等操作。
 **/
@Service
public class ParkingLotService {

    @Resource
    private ParkingLotMapper parkingLotMapper; // 注入ParkingLotMapper用于数据库操作

    @Resource
    private LocationService locationService; // 注入LocationService用于操作停车区域信息

    /**
     * 添加车位。
     * @param parkingLot 车位实体
     */
    public void add(ParkingLot parkingLot) {
        parkingLotMapper.insert(parkingLot); // 插入数据库
        Location location = locationService.selectById(parkingLot.getLocationId());
        // 新增一个车位，同步该区域的总车位和空闲车位
        if (ObjUtil.isNotEmpty(location)) {
            location.setTotal(location.getTotal() + 1);
            if ("空闲".equals(parkingLot.getStatus())) {
                location.setNum(location.getNum() + 1);
            }
            locationService.updateById(location); // 更新停车区域信息
        }
    }

    /**
     * 根据ID删除车位。
     * @param id 车位ID
     */
    public void deleteById(Integer id) {
        ParkingLot parkingLot = parkingLotMapper.selectById(id); // 查询操作
        parkingLotMapper.deleteById(id); // 删除操作
        Location location = locationService.selectById(parkingLot.getLocationId());
        location.setTotal(location.getTotal() - 1);
        if ("空闲".equals(parkingLot.getStatus())) {
            location.setNum(location.getNum() - 1);
        }
        locationService.updateById(location); // 更新停车区域信息
    }

    /**
     * 批量删除车位。
     * @param ids 车位ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            parkingLotMapper.deleteById(id); // 批量删除操作
        }
    }

    /**
     * 更新车位信息。
     * @param parkingLot 车位实体
     */
    public void updateById(ParkingLot parkingLot) {
        ParkingLot dbLot = parkingLotMapper.selectById(parkingLot.getId()); // 查询操作
        parkingLotMapper.updateById(parkingLot); // 更新操作
        // 判断是否需要更新该区域的空闲车位状态
        Location location = locationService.selectById(parkingLot.getLocationId());
        if ("空闲".equals(dbLot.getStatus()) && "占用".equals(parkingLot.getStatus())) {
            location.setNum(location.getNum() - 1);
        }
        if ("占用".equals(dbLot.getStatus()) && "空闲".equals(parkingLot.getStatus())) {
            location.setNum(location.getNum() + 1);
        }
        locationService.updateById(location); // 更新停车区域信息
    }

    /**
     * 根据ID查询车位。
     * @param id 车位ID
     * @return 车位实体
     */
    public ParkingLot selectById(Integer id) {
        return parkingLotMapper.selectById(id); // 查询操作
    }

    /**
     * 查询所有车位。
     * @param parkingLot 车位实体（可选条件）
     * @return 车位列表
     */
    public List<ParkingLot> selectAll(ParkingLot parkingLot) {
        return parkingLotMapper.selectAll(parkingLot); // 查询所有操作
    }

    /**
     * 分页查询车位。
     * @param parkingLot 车位实体（可选条件）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageInfo<ParkingLot> selectPage(ParkingLot parkingLot, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); // 开始分页
        List<ParkingLot> list = this.selectAll(parkingLot); // 查询所有操作
        return PageInfo.of(list); // 返回分页结果
    }

}