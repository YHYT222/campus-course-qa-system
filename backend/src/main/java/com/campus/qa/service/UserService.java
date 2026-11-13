package com.campus.qa.service;

import com.campus.qa.entity.User;

import java.util.Map;

/**
 * 用户业务接口：注册、登录、个人信息、账号状态校验
 */
public interface UserService {

    /**
     * 学生注册
     */
    void register(String studentNo, String username, String password);

    /**
     * 登录，返回 token 与用户信息
     */
    Map<String, Object> login(String studentNo, String password);

    /**
     * 获取当前登录用户（已剔除密码字段）
     */
    User getCurrentUser();

    /**
     * 修改个人资料（昵称、头像）
     */
    User updateProfile(Map<String, String> body);

    /**
     * 校验用户是否被禁言，禁言用户不得发帖与回复
     */
    void checkNotMuted(Long userId);
}
