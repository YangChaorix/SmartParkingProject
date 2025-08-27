package com.example.mapper;

import com.example.entity.ParkingLot;

import java.util.List;

/**
 * 操作parking_lot相关数据接口
 */
public interface ParkingLotMapper {

    /**
     * 新增
     */
    int insert(ParkingLot parkingLot);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(ParkingLot parkingLot);

    /**
     * 根据ID查询
     */
    ParkingLot selectById(Integer id);

    /**
     * 查询所有
     */
    List<ParkingLot> selectAll(ParkingLot parkingLot);

}