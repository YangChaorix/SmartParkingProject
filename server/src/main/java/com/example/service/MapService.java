package com.example.service;

import com.example.entity.MapEntity;
import com.example.mapper.MapMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MapService {
    @Resource
    private MapMapper mapMapper;

    /**
     * 查询所有地图标点信息
     * @return 地图标点信息
     */
    public List<MapEntity> selectAll() {
        return mapMapper.selectAll();
    }
}
