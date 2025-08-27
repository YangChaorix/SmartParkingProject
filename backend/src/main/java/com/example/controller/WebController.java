package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.service.AdminService;
import com.example.utils.MailUtils;
import com.example.service.UserService;
import com.example.utils.RedisUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.Map;
import java.util.UUID;
import java.util.Random;

@RestController
public class WebController {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private MailUtils mailUtils;

    /**
     * 默认测试请求接口
     */
    @GetMapping("/")
    public Result hello() {
        return Result.success();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        // 在实际应用中，你可以在这里添加验证码验证逻辑
         String captchaId = account.getCaptchaId();
         String userInputCaptcha = account.getCaptchaCode();
         String storedCaptcha = redisUtils.getCaptcha(captchaId);
        if (!StringUtils.hasLength(storedCaptcha) || !StringUtils.hasText(userInputCaptcha) || !storedCaptcha.equalsIgnoreCase(userInputCaptcha)) {
            return Result.error("验证码错误或已过期");
        }
         redisUtils.deleteCaptcha(captchaId); // 验证成功后删除验证码

        Account loginAccount = null;
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            loginAccount = adminService.login(account);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            loginAccount = userService.login(account);
        }
        return Result.success(loginAccount);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        // 邮箱验证码校验逻辑
        // 1. 邮箱验证码校验逻辑
        String userEmail = account.getEmail(); // 获取用户注册邮箱
        String userInputEmailCode = account.getVerifyCode(); // 获取用户输入的邮箱验证码

        // 从 Redis 中根据邮箱获取之前存储的验证码
        String storedEmailCode = redisUtils.getVerifyCode(userEmail);

        // 2. 验证码比对
        // 检查 Redis 中是否存在验证码，并且用户输入的验证码是否与存储的匹配
        if (!StringUtils.hasLength(storedEmailCode) || !StringUtils.hasText(storedEmailCode) || !storedEmailCode.equalsIgnoreCase(userInputEmailCode)) {
            // 如果验证码为空或不匹配，则返回错误
            return Result.error("邮箱验证码错误或已过期");
        }

        // 3. 验证通过，删除 Redis 中的验证码
        // 这样做可以防止同一个验证码被重复使用，提高安全性
        redisUtils.deleteVerifyCode(userEmail);

        if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.register(account);
        }
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        System.out.println("请求参数角色："+ account.getRole());
        if (RoleEnum.ADMIN.name().equals(account.getRole())) {
            adminService.updatePassword(account);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        }
        return Result.success();
    }

    /**
     * 生成验证码接口
     */
    @GetMapping("/api/captcha")
    public Result getCaptcha() {
        // 生成线段干扰的验证码，字符数量为4
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 48, 4, 5);
        String code = lineCaptcha.getCode(); // 验证码文本
        String img = lineCaptcha.getImageBase64(); // 验证码图片的Base64编码

        // 为验证码生成一个唯一的ID
        String captchaId = UUID.randomUUID().toString();

        // 使用封装好的 RedisService 存储验证码
        redisUtils.saveCaptcha(captchaId, code);

        // 将验证码ID和图片返回给前端
        Map<String, String> response = Map.of(
                "captchaId", captchaId,
                "img", img
        );
        return Result.success(response);
    }

    /**
     * 生成并发送邮箱验证码接口
     */
    @PostMapping("/api/sendVerifyCode")
    public Result sendVerifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (!StringUtils.hasLength(email) || !StringUtils.hasText(email)) {
            return Result.error("邮箱地址不能为空");
        }

        // 检查是否在发送冷却期内
        if (!redisUtils.isSendingAllowed(email)) {
            return Result.error("验证码发送频繁，请稍后重试");
        }

        // 生成6位随机验证码
        Random random = new Random();
        String verifyCode = String.format("%06d", random.nextInt(999999));

        // 在这里调用实际的邮件发送服务来发送邮件
        mailUtils.sendEmail(email, "智慧停车验证码", "您的验证码是：" + verifyCode + "，有效期5分钟。");

        // 邮件发送成功后，再执行以下操作
        // 将验证码存入Redis
        redisUtils.saveVerifyCode(email, verifyCode);

        // 设置30秒的发送冷却
        redisUtils.setSendingCoolDown(email);

        return Result.success("邮箱验证码已发送，请注意查收");

    }
}