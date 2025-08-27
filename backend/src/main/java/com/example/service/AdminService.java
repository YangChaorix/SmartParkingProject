package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 管理员业务处理类，负责管理员的增删改查等操作。
 **/
@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper; // 注入AdminMapper用于数据库操作

    /**
     * 添加管理员。
     * @param admin 管理员实体
     */
    public void add(Admin admin) {
        Admin dbAdmin = adminMapper.selectByUsername(admin.getUsername());
        if (ObjectUtil.isNotNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR); // 用户已存在
        }
        if (ObjectUtil.isEmpty(admin.getPassword())) {
            String md5DigestAsHex = DigestUtils.md5DigestAsHex(Constants.USER_DEFAULT_PASSWORD.getBytes());
            admin.setPassword(md5DigestAsHex); // 设置默认密码
        }
        if (ObjectUtil.isEmpty(admin.getName())) {
            admin.setName(admin.getUsername()); // 默认名称为用户名
        }
        admin.setRole(RoleEnum.ADMIN.name()); // 设置角色为管理员
        adminMapper.insert(admin); // 插入数据库
    }

    /**
     * 根据ID删除管理员。
     * @param id 管理员ID
     */
    public void deleteById(Integer id) {
        adminMapper.deleteById(id); // 删除操作
    }

    /**
     * 批量删除管理员。
     * @param ids 管理员ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            adminMapper.deleteById(id); // 批量删除操作
        }
    }

    /**
     * 更新管理员信息。
     * @param admin 管理员实体
     */
    public void updateById(Admin admin) {
        adminMapper.updateById(admin); // 更新操作
    }

    /**
     * 根据ID查询管理员。
     * @param id 管理员ID
     * @return 管理员实体
     */
    public Admin selectById(Integer id) {
        Admin dbAdmin = adminMapper.selectById(id); // 查询操作
        // 生成token
        String tokenData = dbAdmin.getId() + "-" + RoleEnum.ADMIN.name();
        String token = TokenUtils.createToken(tokenData, dbAdmin.getPassword());
        dbAdmin.setToken(token); // 设置token
        return dbAdmin; // 返回管理员实体
    }

    /**
     * 查询所有管理员。
     * @param admin 管理员实体（可选条件）
     * @return 管理员列表
     */
    public List<Admin> selectAll(Admin admin) {
        return adminMapper.selectAll(admin); // 查询所有操作
    }

    /**
     * 分页查询管理员。
     * @param admin 管理员实体（可选条件）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageInfo<Admin> selectPage(Admin admin, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); // 开始分页
        List<Admin> list = adminMapper.selectAll(admin); // 查询所有操作
        return PageInfo.of(list); // 返回分页结果
    }

    /**
     * 管理员登录。
     * @param account 登录信息
     * @return 登录后的管理员实体
     */
    public Admin login(Account account) {
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR); // 用户不存在
        }

        // 对密码进行MD5加密后进行比较
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(account.getPassword().getBytes());

        if (!md5DigestAsHex.equals(dbAdmin.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR); // 账号或密码错误
        }
        // 生成token
        String tokenData = dbAdmin.getId() + "-" + RoleEnum.ADMIN.name();
        String token = TokenUtils.createToken(tokenData, dbAdmin.getPassword());
        dbAdmin.setToken(token); // 设置token

        return dbAdmin; // 返回管理员实体
    }

    /**
     * 修改密码。
     * @param account 修改密码的登录信息
     */
    public void updatePassword(Account account) {
        Admin dbAdmin = adminMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR); // 用户不存在
        }
        // MD5加密
        String md5Password = DigestUtils.md5DigestAsHex(account.getPassword().getBytes());
        if (!md5Password.equals(dbAdmin.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR); // 原密码输入错误
        }
        // System.out.println("管理员新密码：" + account.getNewPassword());
        // 更新MD5加密
        dbAdmin.setPassword(DigestUtils.md5DigestAsHex(account.getNewPassword().getBytes()));
        adminMapper.updateById(dbAdmin); // 更新操作
    }

}