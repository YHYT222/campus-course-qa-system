package com.campus.qa.interceptor;

import com.campus.qa.dto.Result;
import com.campus.qa.entity.User;
import com.campus.qa.mapper.UserMapper;
import com.campus.qa.utils.TokenHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录与权限拦截器
 * 1. 所有 /api/** 接口（除注册、登录、课程分类列表外）均要求携带有效 token；
 * 2. /api/admin/** 接口额外校验角色必须为管理员，杜绝学生越权访问后台（需求 5.2 权限隔离）。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 跨域预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 从请求头获取登录令牌
        String token = request.getHeader("token");
        Long userId = TokenHolder.getUserId(token);
        if (userId == null) {
            writeJson(response, 401, "未登录或登录已失效，请重新登录");
            return false;
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            writeJson(response, 401, "用户不存在，请重新登录");
            return false;
        }
        // 后台管理接口必须管理员身份
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/api/admin") && !"admin".equals(user.getRole())) {
            writeJson(response, 403, "无权限访问：该接口仅限管理员");
            return false;
        }
        // 将当前用户信息放入 request，供 Controller 使用
        request.setAttribute("userId", userId);
        request.setAttribute("role", user.getRole());
        return true;
    }

    /** 以 JSON 格式输出拦截结果 */
    private void writeJson(HttpServletResponse response, int code, String msg) throws Exception {
        response.setStatus(code == 401 ? 401 : 403);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(Result.fail(code, msg)));
    }
}
