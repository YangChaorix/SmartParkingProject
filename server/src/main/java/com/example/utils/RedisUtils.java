package com.example.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RedisUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 验证码的 Redis 键前缀
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    // 验证码的过期时间，这里设置为5分钟
    private static final long CAPTCHA_EXPIRATION_SECONDS = 300;

    // 邮箱验证码的 Redis 哈希键前缀
    private static final String VERIFICATION_KEY_PREFIX = "verification:";
    // 邮箱验证码的过期时间，这里设置为5分钟
    private static final long VERIFICATION_EXPIRATION_SECONDS = 300;

    // 验证码发送冷却时间，这里设置为30秒
    private static final long COOL_DOWN_SECONDS = 30;

    /**
     * 将验证码存入 Redis
     *
     * @param captchaId 验证码ID
     * @param captchaCode 验证码文本
     */
    public void saveCaptcha(String captchaId, String captchaCode) {
        String key = CAPTCHA_KEY_PREFIX + captchaId;
        stringRedisTemplate.opsForValue().set(key, captchaCode, CAPTCHA_EXPIRATION_SECONDS, TimeUnit.SECONDS);
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

    /**
     * 将邮箱验证码存入 Redis 哈希，并设置过期时间
     *
     * @param subject 邮箱/手机
     * @param code 验证码
     */
    public void saveVerifyCode(String subject, String code) {
        String key = VERIFICATION_KEY_PREFIX + subject;
        stringRedisTemplate.opsForHash().put(key, "code", code);
        // 设置整个哈希键的过期时间
        stringRedisTemplate.expire(key, VERIFICATION_EXPIRATION_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 根据地址从 Redis 哈希获取验证码
     *
     * @param subject 邮箱/手机
     * @return 验证码，如果不存在或已过期则返回 null
     */
    public String getVerifyCode(String subject) {
        String key = VERIFICATION_KEY_PREFIX + subject;
        Object code = stringRedisTemplate.opsForHash().get(key, "code");
        return code == null ? null : code.toString();
    }

    /**
     * 从 Redis 删除验证码
     *
     * @param subject 邮箱/手机
     */
    public void deleteVerifyCode(String subject) {
        String key = VERIFICATION_KEY_PREFIX + subject;
        stringRedisTemplate.delete(key);
    }

    /**
     * 检查邮箱是否可以发送验证码（是否在冷却期内）
     *
     * @param subject 邮箱/手机
     * @return true 如果可以发送，false 如果在冷却期内
     */
    public boolean isSendingAllowed(String subject) {
        String key = VERIFICATION_KEY_PREFIX + subject;
        Object cooldownStartObj = stringRedisTemplate.opsForHash().get(key, "cooldown_start");
        if (cooldownStartObj == null) {
            return true; // 第一次发送，允许
        }

        long cooldownStart = Long.parseLong(cooldownStartObj.toString());
        long elapsedTime = System.currentTimeMillis() - cooldownStart;

        // 如果已流逝的时间大于等于冷却时间，则允许发送
        return elapsedTime >= COOL_DOWN_SECONDS * 1000;
    }

    /**
     * 设置邮箱发送验证码的冷却时间
     *
     * @param subject 邮箱/手机
     */
    public void setSendingCoolDown(String subject) {
        String key = VERIFICATION_KEY_PREFIX + subject;
        // 存储当前时间作为冷却开始时间
        stringRedisTemplate.opsForHash().put(key, "cooldown_start", String.valueOf(System.currentTimeMillis()));
        // 同时为整个哈希键设置一个过期时间，确保最终会被清理
        stringRedisTemplate.expire(key, VERIFICATION_EXPIRATION_SECONDS, TimeUnit.SECONDS);
    }
}