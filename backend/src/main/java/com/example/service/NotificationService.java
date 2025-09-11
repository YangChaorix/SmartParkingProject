package com.example.service;

import com.example.entity.Notification;
import com.example.entity.User;
import com.example.mapper.NotificationMapper;
import com.example.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.example.utils.MailUtils;

@Service
public class NotificationService {
    @Resource
    private NotificationMapper notificationMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private MailUtils mailUtils;

    /**
     * 添加通知
     * @param notification 通知对象
     */
    public void add(Notification notification) {
        // 标记 未读 0
        notification.setIsRead(0);
        // 删除时间设为null（未删除）
        notification.setDeletedAt(null);
        // 添加发送时间
        notification.setSendTime(new Date());
        notificationMapper.insert(notification);

        // 查询出邮箱地址
        String email = userMapper.selectById(notification.getUserId()).getEmail();

        if (email != null && !email.trim().isEmpty()) {
            // 发送邮件
            try {
                sendNotification(email, notification);
//                System.out.println("邮件发送成功到: " + email);
            } catch (Exception e) {
                System.err.println("邮件发送失败: " + e.getMessage());
                // 业务抛出异常或记录日志
                throw new RuntimeException("邮件发送失败", e);
            }
        }
    }

    /**
     * 发送邮件
     * @param email 收件人邮箱
     * @param notification 通知对象
     * @throws MessagingException 邮件发送异常
     */
    private void sendNotification(String email, Notification notification) throws MessagingException {

        // 设置邮件主题，如果notification有标题则使用，否则使用默认标题
        String subject = "停车场管理系统通知";

        String emailContent = buildEmailContent(notification);

        mailUtils.sendEmail(email, subject, emailContent);
    }

    /**
     * 构建邮件内容
     * @param notification 通知对象
     * @return HTML格式的邮件内容
     */
    private String buildEmailContent(Notification notification) {
        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>");
        content.append("<html>");
        content.append("<head>");
        content.append("<meta charset='UTF-8'>");
        content.append("<style>");
        content.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        content.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        content.append(".header { background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin-bottom: 20px; }");
        content.append(".content { background-color: #ffffff; padding: 20px; border: 1px solid #e9ecef; border-radius: 5px; }");
        content.append(".footer { margin-top: 20px; padding: 10px; font-size: 12px; color: #6c757d; text-align: center; }");
        content.append("</style>");
        content.append("</head>");
        content.append("<body>");
        content.append("<div class='container'>");
        content.append("<div class='header'>");
        content.append("<h2>系统通知</h2>");
        content.append("</div>");
        content.append("<div class='content'>");

        content.append("<h3> 停车将要超时，需尽快处理！</h3>");

        if (notification.getDescription() != null && !notification.getDescription().trim().isEmpty()) {
            content.append("<p>").append(notification.getDescription()).append("</p>");
        }

//        content.append("<p><strong>发送时间：</strong>").append(notification.getSendTime()).append("</p>");
        Date sendTime = notification.getSendTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String formattedTime = sdf.format(sendTime);

        content.append("<p><strong>发送时间：</strong>").append(formattedTime).append("</p>");

        content.append("</div>");
        content.append("<div class='footer'>");
        content.append("<p>此邮件为系统自动发送，请勿回复。</p>");
        content.append("</div>");
        content.append("</div>");
        content.append("</body>");
        content.append("</html>");

        return content.toString();
    }

    /**
     * 删除通知（软删除）
     * @param id 通知ID
     */
    public void deleteById(Integer id) {
        notificationMapper.deleteById(id);
    }

    /**
     * 批量删除通知（软删除）
     * @param ids 批量通知ID
     */
    public void deleteBatch(List<Integer> ids) {
        notificationMapper.deleteBatch(ids);
    }

    /**
     * 更新通知
     * @param notification 通知对象
     */
    public void updateById(Notification notification) {
        notificationMapper.updateById(notification);
    }

    /**
     * 根据ID查询通知
     * @param id 通知ID
     * @return 通知对象
     */
    public Notification selectById(Integer id) {
        return notificationMapper.selectById(id);
    }

    /**
     * 查询所有通知
     * @param notification 查询条件
     * @return 通知列表
     */
    public List<Notification> selectAll(Notification notification) {
        List<Notification> notifications = notificationMapper.selectAll(notification);
        for (Notification n : notifications) {
            User user = userMapper.selectById(n.getUserId());
            n.setUsername(user.getUsername());
        }
        return notifications;
    }

    /**
     * 分页查询通知
     * @param notification 查询条件
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public PageInfo<Notification> selectPage(Notification notification, Integer pageNum, Integer pageSize) {
        PageInfo<Notification> page = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> notificationMapper.selectAll(notification));
        for (Notification n : page.getList()) {
            User user = userMapper.selectById(n.getUserId());
            n.setUsername(user.getUsername());
        }
        return page;
    }

    /**
     * 分页查询用户通知（按时间倒序，只查询未删除的通知）
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    public PageInfo<Notification> selectUserNotifications(Integer userId, Integer pageNum, Integer pageSize) {
        PageInfo<Notification> page = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> notificationMapper.selectUserNotifications(userId));
        for (Notification n : page.getList()) {
            User user = userMapper.selectById(n.getUserId());
            n.setUsername(user.getUsername());
        }
        return page;
    }

    /**
     * 删除真实通知
     * @param id 通知ID
     */
    public void deleteReal(Integer id) {
        notificationMapper.deleteReal(id);
    }

    /**
     * 查询用户未读通知数量
     * @param userId 用户ID
     * @return 未读通知数量
     */
    public Integer selectUnreadCount(Integer userId) {
        return notificationMapper.selectUnreadCount(userId);
    }
}