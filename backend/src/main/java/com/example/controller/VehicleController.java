package com.example.controller;

import com.example.common.Result;
import com.example.entity.Vehicle;
import com.example.service.VehicleService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 车辆信息前端操作接口
 **/
@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Resource
    private VehicleService vehicleService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Vehicle vehicle) {
        vehicleService.add(vehicle);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        vehicleService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        vehicleService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Vehicle vehicle) {
        vehicleService.updateById(vehicle);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Vehicle vehicle = vehicleService.selectById(id);
        return Result.success(vehicle);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Vehicle vehicle) {
        List<Vehicle> list = vehicleService.selectAll(vehicle);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Vehicle vehicle,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Vehicle> page = vehicleService.selectPage(vehicle, pageNum, pageSize);
        return Result.success(page);
    }

}