package com.campus.qa.service;

import com.campus.qa.dto.LoginVO;
import com.campus.qa.entity.User;
import java.util.List;

/**
 * 用户业务层接口 —— 对应需求 4.1.1 账号注册与登录、4.2.3 用户管理
 */
public interface UserService {

    /** 学生注册 */
    User register(User user);

    /** 登录：校验账号密码后返回令牌与用户信息 */
    LoginVO login(String studentNo, String password);

    /** 根据id查询用户 */
    User getById(Long id);

    /** 修改个人资料（用户名、头像） */
    void updateInfo(Long id, String username, String avatar);

    /** 管理员：按关键字查询用户列表 */
    List<User> listByKeyword(String keyword);

    /** 管理员：注销用户账号（基础账号管控） */
    void deleteUser(Long id);

    /** 管理员：重置用户密码为 123456（基础账号管控） */
    void resetPassword(Long id);

    /** 统计用户总数 */
    Long countAll();
}
