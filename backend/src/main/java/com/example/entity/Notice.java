package com.example.entity;

/**
 * 系统公告实体类
 */
public class Notice {
    // ID
    private Integer id;
    // 标题
    private String title;
    // 内容
    private String content;
    // 发布时间
    private String time;
    // 删除时间
    private String deletedAt;

    // 获取ID
    public Integer getId() {
        return id;
    }

    // 设置ID
    public void setId(Integer id) {
        this.id = id;
    }

    // 获取标题
    public String getTitle() {
        return title;
    }

    // 设置标题
    public void setTitle(String title) {
        this.title = title;
    }

    // 获取内容
    public String getContent() {
        return content;
    }

    // 设置内容
    public void setContent(String content) {
        this.content = content;
    }

    // 获取发布时间
    public String getTime() {
        return time;
    }

    // 设置发布时间
    public void setTime(String time) {
        this.time = time;
    }

    // 获取删除时间
    public String getDeletedAt() {
        return deletedAt;
    }

    // 设置删除时间
    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
}