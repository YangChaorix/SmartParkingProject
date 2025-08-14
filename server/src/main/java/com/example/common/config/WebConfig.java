package com.example.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Spring MVC 配置类，用于配置拦截器。
 **/
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private JwtInterceptor jwtInterceptor; // 注入JwtInterceptor

    /**
     * 添加自定义拦截器JwtInterceptor，并设置拦截规则。
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor) // 添加JwtInterceptor
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns("/") // 排除根路径
                .excludePathPatterns("/login", "/register", "/files/**", "/role/selectAll", "/captcha"); // 排除不需要拦截的路径
    }
}