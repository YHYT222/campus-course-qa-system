package com.campus.qa.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.qa.common.Result;
import com.campus.qa.entity.Course;
import com.campus.qa.entity.User;
import com.campus.qa.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理员端：课程分类管理、帖子审核、用户管理
 * 访问 /api/admin/** 的请求统一由 AuthInterceptor 校验管理员角色
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ==================== 课程分类管理 ====================

    @PostMapping("/course")
    public Result<Void> addCourse(@RequestBody Course course) {
        adminService.addCourse(course);
        return Result.ok();
    }

    @PutMapping("/course")
    public Result<Void> updateCourse(@RequestBody Course course) {
        adminService.updateCourse(course);
        return Result.ok();
    }

    @DeleteMapping("/course/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        adminService.deleteCourse(id);
        return Result.ok();
    }

    // ==================== 帖子审核 ====================

    /**
     * 帖子分页（status: null 全部 / 0 待审核 / 1 已发布 / 2 已屏蔽）
     */
    @GetMapping("/post/page")
    public Result<IPage<Map<String, Object>>> postPage(@RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size,
                                                       @RequestParam(required = false) Integer status,
                                                       @RequestParam(required = false) String keyword) {
        return Result.ok(adminService.postPage(page, size, status, keyword));
    }

    /**
     * 审核操作：0 待审核 / 1 通过发布 / 2 屏蔽
     */
    @PutMapping("/post/{id}/status/{status}")
    public Result<Void> auditPost(@PathVariable Long id, @PathVariable Integer status) {
        adminService.auditPost(id, status);
        return Result.ok();
    }

    /**
     * 删除帖子（级联删除回复、点赞、收藏）
     */
    @DeleteMapping("/post/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        adminService.deletePost(id);
        return Result.ok();
    }

    // ==================== 用户管理 ====================

    @GetMapping("/user/page")
    public Result<IPage<User>> userPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) String keyword) {
        return Result.ok(adminService.userPage(page, size, keyword));
    }

    /**
     * 修改用户状态：0 正常 / 1 禁言 / 2 封禁
     */
    @PutMapping("/user/{id}/status/{status}")
    public Result<Void> changeUserStatus(@PathVariable Long id, @PathVariable Integer status) {
        adminService.changeUserStatus(id, status);
        return Result.ok();
    }

    /**
     * 重置用户密码为 123456
     */
    @PutMapping("/user/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        adminService.resetPassword(id);
        return Result.ok();
    }
}
