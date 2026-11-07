package com.campus.qa.dto;

/**
 * 统一接口返回结果封装
 * code = 0 表示业务成功，非 0 表示业务失败
 */
public class Result<T> {

    /** 状态码：0-成功，其他为失败 */
    private Integer code;
    /** 提示信息 */
    private String msg;
    /** 返回数据 */
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /** 成功，不带数据 */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /** 成功，带数据 */
    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<T>();
        r.setCode(0);
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    /** 成功，带提示语和数据 */
    public static <T> Result<T> ok(String msg, T data) {
        Result<T> r = new Result<T>();
        r.setCode(0);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    /** 失败，默认状态码 1 */
    public static <T> Result<T> fail(String msg) {
        return fail(1, msg);
    }

    /** 失败，自定义状态码 */
    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> r = new Result<T>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
}
