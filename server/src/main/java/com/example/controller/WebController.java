package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.service.AdminService;
import com.example.service.UserService;
import com.example.service.RedisService;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

@RestController
public class WebController {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;
    @Resource
    private RedisService redisService;

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
        // String captchaId = account.getCaptchaId();
        // String userInputCaptcha = account.getCaptchaCode();
        // String storedCaptcha = redisService.getCaptcha(captchaId);
        // if (StringUtils.isEmpty(storedCaptcha) || !storedCaptcha.equalsIgnoreCase(userInputCaptcha)) {
        //     return Result.error("验证码错误或已过期");
        // }
        // redisService.deleteCaptcha(captchaId); // 验证成功后删除验证码

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
    @GetMapping("/captcha")
    public Result getCaptcha() {
        // 生成线段干扰的验证码，字符数量为4
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 48, 4, 5);
        String code = lineCaptcha.getCode(); // 验证码文本
        String img = lineCaptcha.getImageBase64(); // 验证码图片的Base64编码

        // 为验证码生成一个唯一的ID
        String captchaId = UUID.randomUUID().toString();

        // 使用封装好的 RedisService 存储验证码
        redisService.saveCaptcha(captchaId, code);

        // 将验证码ID和图片返回给前端
        Map<String, String> response = Map.of(
                "captchaId", captchaId,
                "img", img
        );
        return Result.success(response);
    }
}