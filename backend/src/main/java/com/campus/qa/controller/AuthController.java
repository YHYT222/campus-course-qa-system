package com.campus.qa.controller;

import com.campus.qa.common.Result;
import com.campus.qa.entity.User;
import com.campus.qa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证与个人信息
 * Controller 只负责接收参数、调用 Service、封装统一返回，业务逻辑全部位于 Service 层
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 学生注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> body) {
        userService.register(trim(body.get("studentNo")),
                trim(body.get("username")),
                trim(body.get("password")));
        return Result.ok();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        return Result.ok(userService.login(trim(body.get("studentNo")), trim(body.get("password"))));
    }

    /**
     * 当前登录用户信息
     */
    @GetMapping("/me")
    public Result<User> me() {
        return Result.ok(userService.getCurrentUser());
    }

    /**
     * 编辑个人信息（昵称、头像）
     */
    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody Map<String, String> body) {
        return Result.ok(userService.updateProfile(body));
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
