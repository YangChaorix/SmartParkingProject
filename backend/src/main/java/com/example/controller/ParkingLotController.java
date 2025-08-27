package com.example.controller;

import com.example.common.Result;
import com.example.entity.ParkingLot;
import com.example.service.ParkingLotService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 车位信息前端操作接口
 **/
@RestController
@RequestMapping("/parkingLot")
public class ParkingLotController {

    @Resource
    private ParkingLotService parkingLotService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody ParkingLot parkingLot) {
        parkingLotService.add(parkingLot);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        parkingLotService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        parkingLotService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody ParkingLot parkingLot) {
        parkingLotService.updateById(parkingLot);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        ParkingLot parkingLot = parkingLotService.selectById(id);
        return Result.success(parkingLot);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(ParkingLot parkingLot) {
        List<ParkingLot> list = parkingLotService.selectAll(parkingLot);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(ParkingLot parkingLot,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<ParkingLot> page = parkingLotService.selectPage(parkingLot, pageNum, pageSize);
        return Result.success(page);
    }

}