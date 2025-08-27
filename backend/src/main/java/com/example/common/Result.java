package com.example.common;

/**
 * 通用的API响应结果封装类。
 */
public class Result {
    private String code; // 响应状态码
    private String msg;  // 响应消息
    private Object data; // 响应数据

    // 私有构造函数，防止外部直接使用new创建对象
    private Result(Object data) {
        this.data = data;
    }

    // 默认构造函数
    public Result() {
    }

    /**
     * 创建一个成功的响应结果。
     * @return 成功的响应结果
     */
    public static Result success() {
        Result result = new Result();
        result.setCode("200"); // 设置状态码为200
        result.setMsg("请求成功"); // 设置消息为请求成功
        return result;
    }

    /**
     * 创建一个成功的响应结果，并附带数据。
     * @param data 响应的数据
     * @return 成功的响应结果
     */
    public static Result success(Object data) {
        Result result = success();
        result.setData(data); // 设置响应数据
        return result;
    }

    /**
     * 创建一个失败的响应结果。
     * @return 失败的响应结果
     */
    public static Result error() {
        Result result = new Result();
        result.setCode("500"); // 设置状态码为500
        result.setMsg("请求失败"); // 设置消息为请求失败
        return result;
    }

    /**
     * 创建一个失败的响应结果，并附带自定义的状态码和消息。
     * @param code 状态码
     * @param msg 消息
     * @return 失败的响应结果
     */
    public static Result error(String code, String msg) {
        Result result = new Result();
        result.setCode(code); // 设置状态码
        result.setMsg(msg); // 设置消息
        return result;
    }

    /**
     * 创建一个失败的响应结果，并附带自定义的消息。
     * @param msg 消息
     * @return 失败的响应结果
     */
    public static Result error(String msg) {
        Result result = new Result();
        result.setCode("500"); // 设置状态码为500
        result.setMsg(msg); // 设置消息
        return result;
    }

    // Getter和Setter方法
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}