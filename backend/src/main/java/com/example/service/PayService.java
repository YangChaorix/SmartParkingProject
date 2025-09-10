package com.example.service;

import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Pay;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.PayMapper;
import com.example.utils.DateTimeUtils;
import com.example.utils.SerialNumberUtils;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 停车费用业务处理类，负责停车费用的增删改查等操作。
 **/
@Service
public class PayService {

    @Resource
    private PayMapper payMapper; // 注入PayMapper用于数据库操作

    @Resource
    private UserService userService; // 注入UserService用于操作用户信息

    /**
     * 添加停车费用记录。
     *
     * @param pay 停车费用实体
     */
    public void add(Pay pay) {
        // 生成唯一订单编号
        if (pay.getSerialNumber() == null || pay.getSerialNumber().isEmpty()) {
            pay.setSerialNumber(SerialNumberUtils.generateSerialNumber());
        }
        payMapper.insert(pay); // 插入数据库
    }

    /**
     * 根据ID删除停车费用记录。
     *
     * @param id 停车费用记录ID
     */
    public void deleteById(Integer id) {
        payMapper.deleteById(id); // 删除操作
    }

    /**
     * 批量删除停车费用记录。
     *
     * @param ids 停车费用记录ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            payMapper.deleteById(id); // 批量删除操作
        }
    }

    /**
     * 更新停车费用记录。
     *
     * @param pay 停车费用实体
     */
    public void updateById(Pay pay) {
        // 查询余额是否足够
        User user = userService.selectById(pay.getUserId());
        if (user.getAccount() < pay.getPrice()) {
            throw new CustomException("500", "您的余额不足，请到个人中心充值");
        }
        user.setAccount(user.getAccount() - pay.getPrice()); // 更新用户余额
        userService.updateById(user); // 更新用户信息

        // 设置支付时间和状态
        pay.setStatus("已缴费"); // 更新缴费状态
        pay.setPayTime(DateTimeUtils.getCurrentDateTime()); // 设置支付时间
        payMapper.updateById(pay); // 更新数据库
    }

    /**
     * 根据ID查询停车费用记录。
     *
     * @param id 停车费用记录ID
     * @return 停车费用实体
     */
    public Pay selectById(Integer id) {
        return payMapper.selectById(id); // 查询操作
    }

    /**
     * 查询所有停车费用记录。
     *
     * @param pay 停车费用实体（可选条件）
     * @return 停车费用列表
     */
    public List<Pay> selectAll(Pay pay) {
        return payMapper.selectAll(pay); // 查询所有操作
    }

    /**
     * 分页查询停车费用记录。
     *
     * @param pay    停车费用实体（可选条件）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageInfo<Pay> selectPage(Pay pay, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            pay.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize); // 开始分页
        List<Pay> list = this.selectAll(pay); // 查询所有操作
        return PageInfo.of(list); // 返回分页结果
    }


}