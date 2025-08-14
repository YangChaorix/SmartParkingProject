package com.example.entity;

/**
 * 管理员实体类
 */
public class Admin extends Account {

    // ID
    private Integer id;
    // 账号
    private String username;
    // 密码
    private String password;
    // 名称
    private String name;
    // 头像
    private String avatar;
    // 角色标识
    private String role;
    // 电话
    private String phone;
    // 邮箱
    private String email;

    // 获取ID
    public Integer getId() {
        return id;
    }

    // 设置ID
    public void setId(Integer id) {
        this.id = id;
    }

    // 获取账号
    @Override
    public String getUsername() {
        return username;
    }

    // 设置账号
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    // 获取密码
    @Override
    public String getPassword() {
        return password;
    }

    // 设置密码
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    // 获取名称
    public String getName() {
        return name;
    }

    // 设置名称
    public void setName(String name) {
        this.name = name;
    }

    // 获取头像
    public String getAvatar() {
        return avatar;
    }

    // 设置头像
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    // 获取角色标识
    @Override
    public String getRole() {
        return role;
    }

    // 设置角色标识
    @Override
    public void setRole(String role) {
        this.role = role;
    }

    // 获取电话
    public String getPhone() {
        return phone;
    }

    // 设置电话
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // 获取邮箱
    public String getEmail() {
        return email;
    }

    // 设置邮箱
    public void setEmail(String email) {
        this.email = email;
    }
}