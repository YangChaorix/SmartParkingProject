package com.example.controller;

import com.example.common.Result;
import com.example.entity.Location;
import com.example.service.LocationService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 停车区域表前端操作接口
 **/
@RestController
@RequestMapping("/location")
public class LocationController {

    @Resource
    private LocationService locationService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Location location) {
        locationService.add(location);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        locationService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        locationService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Location location) {
        locationService.updateById(location);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Location location = locationService.selectById(id);
        return Result.success(location);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Location location) {
        List<Location> list = locationService.selectAll(location);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Location location,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Location> page = locationService.selectPage(location, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 地图
     */
    @GetMapping("/map")
    public Result map() {
        List<Location> list = locationService.selectMany();
        return Result.success(list);
    }

}