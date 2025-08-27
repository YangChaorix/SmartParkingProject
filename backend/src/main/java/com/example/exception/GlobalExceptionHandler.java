package com.example.exception;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器，用于统一处理控制器中抛出的异常。
 */
@ControllerAdvice(basePackages="com.example.controller")
public class GlobalExceptionHandler {

    // 创建日志记录器
    private static final Log log = LogFactory.get();

    /**
     * 处理所有未被捕获的异常。
     * @param request 当前请求对象
     * @param e 异常对象
     * @return 封装了错误信息的Result对象
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody // 返回JSON格式的响应
    public Result error(HttpServletRequest request, Exception e){
        // 记录异常信息
        log.error("异常信息：", e);
        // 返回统一的错误结果
        return Result.error();
    }

    /**
     * 处理自定义异常。
     * @param request 当前请求对象
     * @param e 自定义异常对象
     * @return 封装了错误代码和错误消息的Result对象
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody // 返回JSON格式的响应
    public Result customError(HttpServletRequest request, CustomException e){
        // 返回包含自定义错误代码和消息的结果
        return Result.error(e.getCode(), e.getMsg());
    }
}