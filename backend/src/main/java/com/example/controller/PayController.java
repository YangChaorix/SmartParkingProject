package com.example.controller;

import com.example.common.Result;
import com.example.entity.Pay;
import com.example.service.PayService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 缴费信息前端操作接口
 **/
@RestController
@RequestMapping("/pay")
public class PayController {

    @Resource
    private PayService payService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Pay pay) {
        payService.add(pay);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        payService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        payService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Pay pay) {
        payService.updateById(pay);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Pay pay = payService.selectById(id);
        return Result.success(pay);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Pay pay) {
        List<Pay> list = payService.selectAll(pay);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Pay pay,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Pay> page = payService.selectPage(pay, pageNum, pageSize);
        return Result.success(page);
    }

}