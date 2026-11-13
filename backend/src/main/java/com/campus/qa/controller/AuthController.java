package com.campus.qa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.qa.common.BusinessException;
import com.campus.qa.common.Result;
import com.campus.qa.common.TokenStore;
import com.campus.qa.common.UserContext;
import com.campus.qa.entity.User;
import com.campus.qa.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证与个人信息
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /** 密码加盐 */
    private static final String SALT = "campus";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenStore tokenStore;

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> body) {
        String studentNo = trim(body.get("studentNo"));
        String username = trim(body.get("username"));
        String password = trim(body.get("password"));

        if (!StringUtils.hasText(studentNo) || !StringUtils.hasText(password)) {
            throw new BusinessException("学号和密码不能为空");
        }
        if (password.length() < 6) {
            throw new BusinessException("密码长度不能少于6位");
        }
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getStudentNo, studentNo));
        if (count > 0) {
            throw new BusinessException("该学号已被注册");
        }

        User user = new User();
        user.setStudentNo(studentNo);
        user.setUsername(StringUtils.hasText(username) ? username : studentNo);
        user.setPassword(md5(password));
        user.setRole(User.ROLE_STUDENT);
        user.setStatus(User.STATUS_NORMAL);
        userMapper.insert(user);
        return Result.ok();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String studentNo = trim(body.get("studentNo"));
        String password = trim(body.get("password"));

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getStudentNo, studentNo));
        if (user == null || !user.getPassword().equals(md5(password))) {
            throw new BusinessException("学号或密码错误");
        }
        if (user.getStatus() == User.STATUS_BANNED) {
            throw new BusinessException("该账号已被封禁，请联系管理员");
        }

        String token = tokenStore.create(user.getId());
        user.setPassword(null);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.ok(data);
    }

    /**
     * 当前登录用户信息
     */
    @GetMapping("/me")
    public Result<User> me() {
        User user = userMapper.selectById(UserContext.get());
        user.setPassword(null);
        return Result.ok(user);
    }

    /**
     * 编辑个人信息（昵称、头像）
     */
    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody Map<String, String> body) {
        User user = userMapper.selectById(UserContext.get());
        if (StringUtils.hasText(body.get("username"))) {
            user.setUsername(trim(body.get("username")));
        }
        if (body.containsKey("avatar")) {
            user.setAvatar(body.get("avatar"));
        }
        userMapper.updateById(user);
        user.setPassword(null);
        return Result.ok(user);
    }

    private static String md5(String raw) {
        return DigestUtils.md5DigestAsHex((raw + SALT).getBytes(StandardCharsets.UTF_8));
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }
}
