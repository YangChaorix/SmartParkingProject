package com.example.entity;

import java.util.Date;

/**
 * 通知实体类
 */
public class Notification {

    // 通知ID
    private Integer id;
    // 用户ID
    private Integer userId;
    // 通知详细描述
    private String description;
    // 是否已读(0-未读,1-已读)
    private Integer isRead;
    // 发送时间
    private Date sendTime;
    // 逻辑删除标记(0-未删除,1-已删除)
    private Integer deleted;

    // 用户名  -- 用于查询用户名  -- 不属于本数据库里的字段
    private String username;

    public Notification() {
    }

    public Notification(Integer id, Integer userId, String description, Integer isRead, Date sendTime, Integer deleted, String username) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.isRead = isRead;
        this.sendTime = sendTime;
        this.deleted = deleted;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", description='" + description + '\'' +
                ", isRead=" + isRead +
                ", sendTime=" + sendTime +
                ", deleted=" + deleted +
                ", username='" + username + '\'' +
                '}';
    }
}