package com.example.service;

import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Vehicle;
import com.example.mapper.VehicleMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 车辆信息业务处理类，负责车辆信息的增删改查等操作。
 **/
@Service
public class VehicleService {

    @Resource
    private VehicleMapper vehicleMapper; // 注入VehicleMapper用于数据库操作

    /**
     * 添加车辆信息。
     *
     * @param vehicle 车辆实体
     */
    public void add(Vehicle vehicle) {
        vehicleMapper.insert(vehicle); // 插入数据库
    }

    /**
     * 根据ID删除车辆信息（软删除）。
     *
     * @param id 车辆信息ID
     */
    public void deleteById(Integer id) {
        vehicleMapper.softDeleteById(id); // 软删除操作
    }

    /**
     * 批量删除车辆信息（软删除）。
     *
     * @param ids 车辆信息ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            vehicleMapper.softDeleteById(id); // 批量软删除操作
        }
    }

    /**
     * 更新车辆信息。
     *
     * @param vehicle 车辆实体
     */
    public void updateById(Vehicle vehicle) {
        vehicleMapper.updateById(vehicle); // 更新操作
    }

    /**
     * 根据ID查询车辆信息。
     *
     * @param id 车辆信息ID
     * @return 车辆实体
     */
    public Vehicle selectById(Integer id) {
        return vehicleMapper.selectById(id); // 查询操作
    }

    /**
     * 查询所有车辆信息。
     *
     * @param vehicle 车辆实体（可选条件）
     * @return 车辆列表
     */
    public List<Vehicle> selectAll(Vehicle vehicle) {
        return vehicleMapper.selectAll(vehicle); // 查询所有操作
    }

    /**
     * 分页查询车辆信息。
     *
     * @param vehicle 车辆实体（可选条件）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageInfo<Vehicle> selectPage(Vehicle vehicle, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser(); // 获取当前用户
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            vehicle.setUserId(currentUser.getId()); // 根据用户角色设置查询条件
        }
        PageHelper.startPage(pageNum, pageSize); // 开始分页
        List<Vehicle> list = this.selectAll(vehicle); // 查询所有操作
        return PageInfo.of(list); // 返回分页结果
    }

}