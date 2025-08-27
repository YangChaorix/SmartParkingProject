package com.example.mapper;

import com.example.entity.Vehicle;

import java.util.List;

/**
 * 操作vehicle相关数据接口
 */
public interface VehicleMapper {

    /**
     * 新增
     */
    int insert(Vehicle vehicle);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Vehicle vehicle);

    /**
     * 根据ID查询
     */
    Vehicle selectById(Integer id);

    /**
     * 查询所有
     */
    List<Vehicle> selectAll(Vehicle vehicle);

}