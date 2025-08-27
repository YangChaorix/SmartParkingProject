package com.example.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.entity.Location;
import com.example.entity.Parking;
import com.example.entity.ParkingLot;
import com.example.service.LocationService;
import com.example.service.ParkingLotService;
import com.example.service.ParkingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表盘接口
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Resource
    private ParkingService parkingService;
    @Resource
    private ParkingLotService parkingLotService;
    @Resource
    private LocationService locationService;

    @GetMapping("/line")
    public Result line() {
        Map<String, Object> map = new HashMap<>();
        // 获取最近7天的数据（年-月-日）放在xList里
        Date today = new Date();
        DateTime start = DateUtil.offsetDay(today, -7);
        List<String> xList = DateUtil.rangeToList(start, today, DateField.DAY_OF_YEAR)
                .stream()
                .map(DateUtil::formatDate)
                .collect(Collectors.toList());

        // 查询出所有的车辆入场记录
        List<Parking> list = parkingService.selectAll(new Parking());
        List<Long> yList = new ArrayList<>();
        for (String day : xList) {
            long count = list.stream().filter(x -> x.getStartTime().contains(day)).count();
            yList.add(count);
        }

        map.put("xList", xList);
        map.put("yList", yList);
        return Result.success(map);
    }

    @GetMapping("/pie")
    public Result pie() {
        List<Map<String, Object>> list = new ArrayList<>();
        // 查询出所有的车位信息
        List<ParkingLot> parkingLots = parkingLotService.selectAll(new ParkingLot());

        Map<String, Object> map = new HashMap<>();
        map.put("name", "空闲");
        map.put("value", parkingLots.stream().filter(x -> "空闲".equals(x.getStatus())).count());
        list.add(map);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "占用");
        map2.put("value", parkingLots.stream().filter(x -> "占用".equals(x.getStatus())).count());
        list.add(map2);

        return Result.success(list);
    }

    @GetMapping("/bar")
    public Result bar() {
        List<String> xList = new ArrayList<>();
        List<Long> yList = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();

        // 查询出所有停车区域
        List<Location> locations = locationService.selectAll(new Location());
        // 查询出所有停车位
        List<ParkingLot> parkingLots = parkingLotService.selectAll(new ParkingLot());
        for (Location location : locations) {
            xList.add(location.getName());
            yList.add(parkingLots.stream().filter(x -> x.getLocationId().equals(location.getId())).count());
        }

        map.put("xList", xList);
        map.put("yList", yList);
        return Result.success(map);
    }
}