package com.campus.qa.controller;

import com.campus.qa.dto.LoginVO;
import com.campus.qa.dto.Result;
import com.campus.qa.entity.User;
import com.campus.qa.service.UserService;
import com.campus.qa.utils.TokenHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户接口 —— 对应需求 4.1.1 账号注册与登录功能
 */
@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    /** 学生注册：POST /api/user/register */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        User saved = userService.register(user);
        // 注册成功不返回密码
        saved.setPassword(null);
        return Result.ok("注册成功", saved);
    }

    /** 登录：POST /api/user/login */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody Map<String, String> params) {
        String studentNo = params.get("studentNo");
        String password = params.get("password");
        LoginVO vo = userService.login(studentNo, password);
        return Result.ok("登录成功", vo);
    }

    /** 获取当前登录用户信息：GET /api/user/info */
    @GetMapping("/info")
    public Result<User> info(HttpServletRequest request) {
        User user = userService.getById(getUserId(request));
        if (user != null) {
            user.setPassword(null);
        }
        return Result.ok(user);
    }

    /** 修改个人资料（用户名、头像）：PUT /api/user/update */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody User user, HttpServletRequest request) {
        userService.updateInfo(getUserId(request), user.getUsername(), user.getAvatar());
        return Result.ok("修改成功", null);
    }

    /** 退出登录：POST /api/user/logout */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        TokenHolder.remove(request.getHeader("token"));
        return Result.ok("已退出登录", null);
    }
}
