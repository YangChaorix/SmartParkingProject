package com.example.service;

import com.example.entity.Location;
import com.example.entity.MapEntity;
import com.example.entity.ParkingLot;
import com.example.exception.CustomException;
import com.example.mapper.LocationMapper;
import com.example.mapper.ParkingLotMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 停车区域业务处理类，负责停车区域的增删改查等操作。
 **/
@Service
public class LocationService {

    @Resource
    private LocationMapper locationMapper; // 注入LocationMapper用于数据库操作

    @Resource
    private ParkingLotMapper parkingLotMapper; // 注入ParkingLotMapper用于数据库操作

    /**
     * 添加停车区域。
     * @param location 停车区域实体
     */
    public void add(Location location) {
        locationMapper.insert(location); // 插入数据库
    }

    /**
     * 根据ID删除停车区域。
     * @param id 停车区域ID
     */
    public void deleteById(Integer id) {
        Location location = locationMapper.selectById(id); // 查询操作
        if (location.getTotal() > 0) {
            throw new CustomException("500", "该区域有停车位信息，暂不支持删除"); // 检查是否有关联的停车位信息
        }
        locationMapper.deleteById(id); // 删除操作
    }

    /**
     * 批量删除停车区域。
     * @param ids 停车区域ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            locationMapper.deleteById(id); // 批量删除操作
        }
    }

    /**
     * 更新停车区域信息。
     * @param location 停车区域实体
     */
    public void updateById(Location location) {
        locationMapper.updateById(location); // 更新操作
    }

    /**
     * 根据ID查询停车区域。
     * @param id 停车区域ID
     * @return 停车区域实体
     */
    public Location selectById(Integer id) {
        return locationMapper.selectById(id); // 查询操作
    }

    /**
     * 查询所有停车区域。
     * @param location 停车区域实体（可选条件）
     * @return 停车区域列表
     */
    public List<Location> selectAll(Location location) {
        List<Location> locations = locationMapper.selectAll(location); // 查询所有操作
        for (Location dbLocation : locations) {
            ParkingLot parkingLot = new ParkingLot();
            parkingLot.setLocationId(dbLocation.getId());
            List<ParkingLot> parkingLots = parkingLotMapper.selectAll(parkingLot);
            dbLocation.setParkingLots(parkingLots); // 设置关联的停车位信息
        }
        return locations; // 返回停车区域列表
    }

    /**
     * 分页查询停车区域。
     * @param location 停车区域实体（可选条件）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageInfo<Location> selectPage(Location location, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); // 开始分页
        List<Location> list = this.selectAll(location); // 查询所有操作
        return PageInfo.of(list); // 返回分页结果
    }

    /**
     * 查询所有地图标点信息
     * @return 地图标点信息
     */
    public List<Location> selectMany() {
        return locationMapper.selectMany();
    }

}