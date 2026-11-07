package com.campus.qa.common;

import lombok.Data;

/**
 * 统一响应结果
 */
@Data
public class Result<T> {

    public static final int SUCCESS = 200;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int ERROR = 500;

    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> ok() {
        return build(SUCCESS, "操作成功", null);
    }

    public static <T> Result<T> ok(T data) {
        return build(SUCCESS, "操作成功", data);
    }

    public static <T> Result<T> error(String msg) {
        return build(ERROR, msg, null);
    }

    public static <T> Result<T> error(int code, String msg) {
        return build(code, msg, null);
    }

    private static <T> Result<T> build(int code, String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
}
