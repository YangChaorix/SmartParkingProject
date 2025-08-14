package com.example.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 验证码的 Redis 键前缀
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    // 验证码的过期时间，这里设置为5分钟
    private static final long CAPTCHA_EXPIRATION_MINUTES = 5;

    /**
     * 将验证码存入 Redis
     *
     * @param captchaId 验证码ID
     * @param captchaCode 验证码文本
     */
    public void saveCaptcha(String captchaId, String captchaCode) {
        String key = CAPTCHA_KEY_PREFIX + captchaId;
        stringRedisTemplate.opsForValue().set(key, captchaCode, CAPTCHA_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 根据验证码ID从 Redis 获取验证码
     *
     * @param captchaId 验证码ID
     * @return 验证码文本，如果不存在或已过期则返回 null
     */
    public String getCaptcha(String captchaId) {
        String key = CAPTCHA_KEY_PREFIX + captchaId;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 从 Redis 删除验证码
     *
     * @param captchaId 验证码ID
     */
    public void deleteCaptcha(String captchaId) {
        String key = CAPTCHA_KEY_PREFIX + captchaId;
        stringRedisTemplate.delete(key);
    }
}