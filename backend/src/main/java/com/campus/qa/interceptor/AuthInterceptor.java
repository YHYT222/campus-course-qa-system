package com.campus.qa.interceptor;

import com.campus.qa.common.Result;
import com.campus.qa.common.TokenStore;
import com.campus.qa.common.UserContext;
import com.campus.qa.entity.User;
import com.campus.qa.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器：校验token；/api/admin/**、/api/stats/** 额外校验管理员角色
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        Long userId = tokenStore.get(token);
        if (userId == null) {
            writeJson(response, Result.UNAUTHORIZED, "请先登录");
            return false;
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            writeJson(response, Result.UNAUTHORIZED, "登录已失效，请重新登录");
            return false;
        }

        String uri = request.getRequestURI();
        boolean adminArea = uri.startsWith("/api/admin/") || uri.startsWith("/api/stats/");
        if (adminArea && user.getRole() != 1) {
            writeJson(response, Result.FORBIDDEN, "无管理员权限");
            return false;
        }

        UserContext.set(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeJson(HttpServletResponse response, int code, String msg) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(code, msg)));
    }
}
