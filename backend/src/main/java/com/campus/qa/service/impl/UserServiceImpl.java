package com.campus.qa.service.impl;

import com.campus.qa.dto.LoginVO;
import com.campus.qa.entity.User;
import com.campus.qa.exception.BusinessException;
import com.campus.qa.mapper.UserMapper;
import com.campus.qa.service.UserService;
import com.campus.qa.utils.Md5Util;
import com.campus.qa.utils.TokenHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户业务层实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /** 管理员角色标识 */
    private static final String ROLE_ADMIN = "admin";
    /** 学生角色标识 */
    private static final String ROLE_STUDENT = "student";
    /** 重置后的默认密码明文 */
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public User register(User user) {
        // 1. 非空校验
        if (user == null || isBlank(user.getStudentNo()) || isBlank(user.getUsername())
                || isBlank(user.getPassword())) {
            throw new BusinessException("学号、用户名、密码均不能为空");
        }
        // 2. 学号查重
        if (userMapper.selectByStudentNo(user.getStudentNo()) != null) {
            throw new BusinessException("该学号已注册，请直接登录");
        }
        // 3. 用户名查重
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException("该用户名已被占用，请更换");
        }
        // 4. 非法字符过滤
        checkIllegal(user.getUsername(), "用户名");
        // 5. 注册用户角色固定为学生，密码 MD5 加密后入库
        user.setRole(ROLE_STUDENT);
        user.setPassword(Md5Util.md5(user.getPassword()));
        userMapper.insert(user);
        return user;
    }

    @Override
    public LoginVO login(String studentNo, String password) {
        if (isBlank(studentNo) || isBlank(password)) {
            throw new BusinessException("账号和密码不能为空");
        }
        User user = userMapper.selectByStudentNo(studentNo);
        if (user == null) {
            throw new BusinessException("账号不存在，请先注册");
        }
        if (!Md5Util.md5(password).equals(user.getPassword())) {
            throw new BusinessException("密码错误，请重新输入");
        }
        // 登录成功，生成令牌
        String token = TokenHolder.createToken(user.getId());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setId(user.getId());
        vo.setStudentNo(user.getStudentNo());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setAvatar(user.getAvatar());
        return vo;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void updateInfo(Long id, String username, String avatar) {
        User db = userMapper.selectById(id);
        if (db == null) {
            throw new BusinessException("用户不存在");
        }
        User update = new User();
        update.setId(id);
        if (!isBlank(username)) {
            checkIllegal(username, "用户名");
            // 用户名被他人占用时不允许修改
            User exist = userMapper.selectByUsername(username);
            if (exist != null && !exist.getId().equals(id)) {
                throw new BusinessException("该用户名已被占用，请更换");
            }
            update.setUsername(username);
        }
        if (!isBlank(avatar)) {
            update.setAvatar(avatar);
        }
        userMapper.update(update);
    }

    @Override
    public List<User> listByKeyword(String keyword) {
        return userMapper.selectByKeyword(keyword);
    }

    @Override
    public void deleteUser(Long id) {
        User db = userMapper.selectById(id);
        if (db == null) {
            throw new BusinessException("用户不存在");
        }
        // 不允许删除管理员账号，避免系统无管理员可用
        if (ROLE_ADMIN.equals(db.getRole())) {
            throw new BusinessException("管理员账号不可注销");
        }
        userMapper.deleteById(id);
    }

    @Override
    public void resetPassword(Long id) {
        User db = userMapper.selectById(id);
        if (db == null) {
            throw new BusinessException("用户不存在");
        }
        User update = new User();
        update.setId(id);
        update.setPassword(Md5Util.md5(DEFAULT_PASSWORD));
        userMapper.update(update);
    }

    @Override
    public Long countAll() {
        return userMapper.countAll();
    }

    /** 判断字符串是否为空 */
    private boolean isBlank(String s) {
        return s == null || s.trim().length() == 0;
    }

    /** 简单非法字符过滤：拦截脚本标签与常见注入关键字 */
    private void checkIllegal(String value, String fieldName) {
        String lower = value.toLowerCase();
        String[] danger = {"<script", "javascript:", "drop ", "delete from", "insert into", "--"};
        for (String d : danger) {
            if (lower.contains(d)) {
                throw new BusinessException(fieldName + "包含非法字符，请重新输入");
            }
        }
    }
}
