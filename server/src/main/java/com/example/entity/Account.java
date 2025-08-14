package com.example.entity;

/**
 * 管理员实体类
 */
public class Account {
    private Integer id;
    // 账号
    private String username;
    // 密码
    private String password;
    // 角色
    private String role;
    // 头像
    private String avatar;
    // 新密码
    private String newPassword;
    // 令牌
    private String token;

    // 获取ID
    public Integer getId() {
        return id;
    }

    // 设置ID
    public void setId(Integer id) {
        this.id = id;
    }

    // 获取账号
    public String getUsername() {
        return username;
    }

    // 设置账号
    public void setUsername(String username) {
        this.username = username;
    }

    // 获取密码
    public String getPassword() {
        return password;
    }

    // 设置密码
    public void setPassword(String password) {
        this.password = password;
    }

    // 获取角色
    public String getRole() {
        return role;
    }

    // 设置角色
    public void setRole(String role) {
        this.role = role;
    }

    // 获取头像
    public String getAvatar() {
        return avatar;
    }

    // 设置头像
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    // 获取新密码
    public String getNewPassword() {
        return newPassword;
    }

    // 设置新密码
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    // 获取令牌
    public String getToken() {
        return token;
    }

    // 设置令牌
    public void setToken(String token) {
        this.token = token;
    }
}