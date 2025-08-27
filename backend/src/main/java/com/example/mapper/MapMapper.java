package com.example.mapper;

import com.example.entity.MapEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface MapMapper {
    @Select("Select * from location_map")
    List<MapEntity> selectAll();
}