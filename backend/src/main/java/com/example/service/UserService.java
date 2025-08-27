package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户业务处理类，负责用户的增删改查等操作。
 **/
@Service
public class UserService {

    @Resource
    private UserMapper userMapper; // 注入UserMapper用于数据库操作

    /**
     * 添加新用户。
     * @param user 用户实体
     */
    public void add(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());
        if (ObjectUtil.isNotNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR); // 用户已存在
        }
        if (ObjectUtil.isEmpty(user.getPassword())) {
            // 设置默认加密处理密码
//            user.setPassword(Constants.USER_DEFAULT_PASSWORD);
            user.setPassword(DigestUtils.md5DigestAsHex(Constants.USER_DEFAULT_PASSWORD.getBytes()));
        }
        if (ObjectUtil.isEmpty(user.getName())) {
            user.setName(user.getUsername()); // 默认名称为用户名
        }
        user.setRole(RoleEnum.USER.name()); // 设置角色为普通用户
        userMapper.insert(user); // 插入数据库
    }

    /**
     * 根据ID删除用户。
     * @param id 用户ID
     */
    public void deleteById(Integer id) {
        userMapper.deleteById(id); // 删除操作
    }

    /**
     * 批量删除用户。
     * @param ids 用户ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            userMapper.deleteById(id); // 批量删除操作
        }
    }

    /**
     * 更新用户信息。
     * @param user 用户实体
     */
    public void updateById(User user) {
        userMapper.updateById(user); // 更新操作
    }

    /**
     * 根据ID查询用户。
     * @param id 用户ID
     * @return 用户实体
     */
    public User selectById(Integer id) {
        User dbUser = userMapper.selectById(id); // 查询操作
        // 生成token
        String tokenData = dbUser.getId() + "-" + RoleEnum.USER.name();
        String token = TokenUtils.createToken(tokenData, dbUser.getPassword());
        dbUser.setToken(token); // 设置token
        return dbUser; // 返回用户实体
    }

    /**
     * 查询所有用户。
     * @param user 用户实体（可选条件）
     * @return 用户列表
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user); // 查询所有操作
    }

    /**
     * 分页查询用户。
     * @param user 用户实体（可选条件）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageInfo<User> selectPage(User user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); // 开始分页
        List<User> list = this.selectAll(user); // 查询所有操作
        return PageInfo.of(list); // 返回分页结果
    }

    /**
     * 用户登录。
     * @param account 登录信息
     * @return 登录后的用户实体
     */
    public User login(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR); // 用户不存在
        }

        // 对密码进行MD5加密后进行比较
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(account.getPassword().getBytes());

        if (!md5DigestAsHex.equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR); // 账号或密码错误
        }

        // 生成token
        String tokenData = dbUser.getId() + "-" + RoleEnum.USER.name();
        String token = TokenUtils.createToken(tokenData, dbUser.getPassword());
        dbUser.setToken(token); // 设置token

        return dbUser; // 返回用户实体
    }

    /**
     * 用户注册。
     * @param account 注册信息
     */
    public void register(Account account) {
        User user = new User();
        user.setUsername(account.getUsername());

        // 2. 判断用户名是否为邮箱格式，如果是则将邮箱字段也设置进去
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(account.getUsername());
        if (matcher.matches()) {
            user.setEmail(account.getUsername());
        }

        // 这里需要对注册的账户密码进行加密处理
        String md5Password = DigestUtils.md5DigestAsHex(account.getPassword().getBytes());
        user.setPassword(md5Password);
        this.add(user); // 调用add方法添加用户
    }

    /**
     * 修改密码。
     * @param account 修改密码的登录信息
     */
    public void updatePassword(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR); // 用户不存在
        }
        // MD5加密
        String md5Password = DigestUtils.md5DigestAsHex(account.getPassword().getBytes());

        if (!md5Password.equals(dbUser.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR); // 原密码输入错误
        }
        if (ObjectUtil.isEmpty(account.getNewPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_ERROR); // 新密码不能为空
        }
        // 更新新的MD5加密
        System.out.println("用户新密码：" + account.getNewPassword());
        dbUser.setPassword(DigestUtils.md5DigestAsHex(account.getNewPassword().getBytes()));
        userMapper.updateById(dbUser); // 更新操作
    }

}