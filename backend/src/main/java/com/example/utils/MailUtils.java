package com.example.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async; // 导入 Async 注解
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class MailUtils {

    // QQ邮箱 SMTP 服务器
    @Value("${mail.smtp.host:smtp.qq.com}")
    private String smtpHost;

    // 邮箱服务器端口 默认 465
    @Value("${mail.smtp.port:465}")
    private int smtpPort;

    // 邮箱服务器用户名
    @Value("${mail.smtp.username:}")
    private String smtpUsername;

    // 邮箱服务器密码
    @Value("${mail.smtp.password:}")
    private String smtpPassword;

    // 发件人邮箱
    @Value("${mail.smtp.from:}")
    private String fromEmail;

    /**
     * 异步发送邮件
     * @param toEmail 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件正文（HTML格式）
     */
    @Async // 新增：使用 @Async 注解使其成为异步方法
    public void sendEmail(String toEmail, String subject, String content) { // 移除 throws MessagingException
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail != null ? fromEmail : smtpUsername));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject, "UTF-8");
            message.setContent(content, "text/html;charset=UTF-8");

            Transport.send(message);
            System.out.println("邮件发送成功：收件人: " + toEmail + ", 主题: " + subject); // 可以在这里打印成功日志
        } catch (MessagingException e) {
            // 在异步方法中，处理异常并记录日志
            System.err.println("邮件发送失败，收件人: " + toEmail + ", 异常信息如下：");
            e.printStackTrace();
            // 在这里不重新抛出异常，防止阻塞调用方
        }
    }
}