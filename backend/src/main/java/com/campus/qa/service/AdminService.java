package com.campus.qa.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.qa.entity.Course;
import com.campus.qa.entity.User;

import java.util.Map;

/**
 * 管理员端业务接口：课程分类管理、帖子审核、用户管理
 */
public interface AdminService {

    // ==================== 课程分类管理 ====================

    void addCourse(Course course);

    void updateCourse(Course course);

    void deleteCourse(Long id);

    // ==================== 帖子审核 ====================

    /**
     * 帖子分页（status 为空表示全部）
     */
    IPage<Map<String, Object>> postPage(Integer page, Integer size, Integer status, String keyword);

    /**
     * 审核帖子：0 待审核 / 1 通过发布 / 2 屏蔽
     */
    void auditPost(Long id, Integer status);

    /**
     * 管理员删除帖子（级联删除回复、点赞、收藏）
     */
    void deletePost(Long id);

    // ==================== 用户管理 ====================

    IPage<User> userPage(Integer page, Integer size, String keyword);

    /**
     * 修改用户状态：0 正常 / 1 禁言 / 2 封禁
     */
    void changeUserStatus(Long id, Integer status);

    /**
     * 重置用户密码为默认值
     */
    void resetPassword(Long id);
}
