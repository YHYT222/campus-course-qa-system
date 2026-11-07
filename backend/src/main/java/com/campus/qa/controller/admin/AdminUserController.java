package com.campus.qa.controller.admin;

import com.campus.qa.dto.Result;
import com.campus.qa.entity.User;
import com.campus.qa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员 - 用户管理接口 —— 对应需求 4.2.3 用户管理功能
 * 说明：用户表字段固定，账号管控通过"重置密码"与"注销账号"两种方式实现
 */
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /** 用户列表（支持关键字搜索）：GET /api/admin/user/list */
    @GetMapping("/list")
    public Result<List<User>> list(@RequestParam(required = false) String keyword) {
        List<User> list = userService.listByKeyword(keyword);
        // 不返回密码字段
        for (User u : list) {
            u.setPassword(null);
        }
        return Result.ok(list);
    }

    /** 重置用户密码为 123456：PUT /api/admin/user/reset/{id} */
    @PutMapping("/reset/{id}")
    public Result<Void> reset(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.ok("密码已重置为 123456", null);
    }

    /** 注销用户账号：DELETE /api/admin/user/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.ok("账号已注销", null);
    }
}
