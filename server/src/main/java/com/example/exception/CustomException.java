package com.example.exception;

import com.example.common.enums.ResultCodeEnum;

/**
 * 自定义异常类，用于封装异常信息，包括错误代码和错误消息。
 * 该异常类继承自 RuntimeException，表示它是一个运行时异常，不需要强制捕获。
 * 提供了两种构造方法，一种是通过枚举类 {@link ResultCodeEnum} 初始化异常信息，
 * 另一种是直接传入错误代码和错误消息。
 */
public class CustomException extends RuntimeException {
    /**
     * 错误代码，用于标识异常的类型。
     */
    private String code;

    /**
     * 错误消息，用于描述异常的具体内容。
     */
    private String msg;

    /**
     * 构造方法，通过枚举类 {@link ResultCodeEnum} 初始化异常信息。
     * @param resultCodeEnum 枚举类实例，包含错误代码和错误消息
     */
    public CustomException(ResultCodeEnum resultCodeEnum) {
        this.code = resultCodeEnum.getCode(); // 获取枚举类中的错误代码
        this.msg = resultCodeEnum.getMsg(); // 获取枚举类中的错误消息
    }

    /**
     * 构造方法，直接传入错误代码和错误消息。
     * @param code 错误代码
     * @param msg 错误消息
     */
    public CustomException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取错误代码。
     * @return 错误代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置错误代码。
     * @param code 错误代码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取错误消息。
     * @return 错误消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置错误消息。
     * @param msg 错误消息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
}