package com.campus.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.qa.common.BusinessException;
import com.campus.qa.common.TokenStore;
import com.campus.qa.common.UserContext;
import com.campus.qa.entity.User;
import com.campus.qa.mapper.UserMapper;
import com.campus.qa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户业务实现
 */
@Service
public class UserServiceImpl implements UserService {

    /** 密码盐值，与数据库初始化脚本保持一致 */
    private static final String SALT = "campus";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void register(String studentNo, String username, String password) {
        if (!StringUtils.hasText(studentNo) || !StringUtils.hasText(password)) {
            throw new BusinessException("学号和密码不能为空");
        }
        if (password.length() < 6) {
            throw new BusinessException("密码长度不能少于6位");
        }
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStudentNo, studentNo));
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
    }

    @Override
    public Map<String, Object> login(String studentNo, String password) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getStudentNo, studentNo));
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
        return data;
    }

    @Override
    public User getCurrentUser() {
        User user = userMapper.selectById(UserContext.get());
        if (user == null) {
            throw new BusinessException("登录状态已失效，请重新登录");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public User updateProfile(Map<String, String> body) {
        User user = userMapper.selectById(UserContext.get());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (StringUtils.hasText(body.get("username"))) {
            user.setUsername(body.get("username").trim());
        }
        if (body.containsKey("avatar")) {
            user.setAvatar(body.get("avatar"));
        }
        userMapper.updateById(user);
        user.setPassword(null);
        return user;
    }

    @Override
    public void checkNotMuted(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null && user.getStatus() == User.STATUS_MUTED) {
            throw new BusinessException("您已被禁言，暂时无法发布内容和回复");
        }
    }

    /**
     * 密码加密：MD5(明文 + 盐)，与 SQL 脚本中的 MD5(CONCAT(密码,'campus')) 一致
     */
    private static String md5(String raw) {
        return DigestUtils.md5DigestAsHex((raw + SALT).getBytes(StandardCharsets.UTF_8));
    }
}
