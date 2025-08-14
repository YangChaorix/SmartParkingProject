package com.example.controller;

import com.example.common.Result;
import com.example.entity.Notification;
import com.example.service.NotificationService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知消息控制器
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    /**
     * 添加通知消息
     */
    @PostMapping("/add")
    public Result add(@RequestBody Notification notification) {
        notificationService.add(notification);
        return Result.success();
    }

    /**
     * 删除通知消息
     */
    @PostMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        // 只实现逻辑删除，而不是实际删除
        notificationService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除通知消息
     */
    @PostMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        // 只实现逻辑删除，而不是实际删除
        notificationService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改通知消息
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Notification notification) {
        notificationService.updateById(notification);
        return Result.success();
    }

    /**
     * 根据id查询通知消息
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Notification notification = notificationService.selectById(id);
        return Result.success(notification);
    }

    /**
     * 根据用户ID查询未读数量
     */
    @GetMapping("/selectUnreadCount/{userId}")
    public Result selectUnreadCount(@PathVariable Integer userId) {
        Integer count = notificationService.selectUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 查询所有通知消息
     */
    @GetMapping("/selectAll")
    public Result selectAll(Notification notification) {
        List<Notification> list = notificationService.selectAll(notification);
        return Result.success(list);
    }

    /**
     * 分页查询通知消息
     */
    @GetMapping("/selectPage")
    public Result selectPage(Notification notification,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Notification> page = notificationService.selectPage(notification, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 管理员真实删除通知信息
     */
    @PostMapping("/deleteReal/{id}")
    public Result deleteReal(@PathVariable Integer id) {
        notificationService.deleteReal(id);
        return Result.success();
    }

    /**
     * 管理员批量真实删除通知信息
     */
    @PostMapping("/deleteReal/batch")
    public Result deleteRealBatch(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            notificationService.deleteReal(id); // 批量删除操作
        }
        return Result.success();
    }

    /**
     * 用户通知标记已读
     */
    @PostMapping("/markAsRead/{id}")
    public Result markAsRead(@PathVariable Integer id) {
        Notification notification = new Notification();
        notification.setId(id);
        // 标记为已读 1
        notification.setIsRead(1);
        notificationService.updateById(notification);
        return Result.success();
    }
}
