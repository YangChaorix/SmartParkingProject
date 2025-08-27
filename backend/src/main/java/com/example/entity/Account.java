package com.example.entity;

/**
 * 账户实体类
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

    // --- 新增字段用于登录和注册 ---
    // 登录图形验证码ID
    private String captchaId;
    // 登录图形验证码
    private String captchaCode;
    // 邮箱地址
    private String email;
    // 邮箱验证码
    private String verifyCode;
    // --- 新增字段结束 ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // --- 新增字段的 getter 和 setter ---
    public String getCaptchaId() {
        return captchaId;
    }

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}