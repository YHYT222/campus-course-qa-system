package com.campus.qa.controller;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller 基类：封装从 request 中读取当前登录用户信息的方法
 * 用户id与角色由 AuthInterceptor 在校验通过后写入 request
 */
public class BaseController {

    /** 获取当前登录用户id */
    protected Long getUserId(HttpServletRequest request) {
        Object v = request.getAttribute("userId");
        if (v == null) {
            return null;
        }
        return Long.valueOf(v.toString());
    }

    /** 判断当前登录用户是否为管理员 */
    protected boolean isAdmin(HttpServletRequest request) {
        return "admin".equals(request.getAttribute("role"));
    }
}
