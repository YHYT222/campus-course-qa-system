package com.campus.qa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.qa.common.BusinessException;
import com.campus.qa.common.Result;
import com.campus.qa.entity.Course;
import com.campus.qa.entity.Post;
import com.campus.qa.entity.User;
import com.campus.qa.mapper.CourseMapper;
import com.campus.qa.mapper.PostMapper;
import com.campus.qa.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 管理员端：课程分类管理、帖子审核、用户管理
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostController postController;

    @Autowired
    private CourseController courseController;

    // ==================== 课程分类管理 ====================

    @PostMapping("/course")
    public Result<Void> addCourse(@RequestBody Course course) {
        courseController.checkCourseNameUnique(course);
        course.setId(null);
        course.setName(course.getName().trim());
        if (course.getDescription() == null) {
            course.setDescription("");
        }
        courseMapper.insert(course);
        return Result.ok();
    }

    @PutMapping("/course")
    public Result<Void> updateCourse(@RequestBody Course course) {
        if (course.getId() == null || courseMapper.selectById(course.getId()) == null) {
            throw new BusinessException("课程不存在");
        }
        courseController.checkCourseNameUnique(course);
        course.setName(course.getName().trim());
        courseMapper.updateById(course);
        return Result.ok();
    }

    @DeleteMapping("/course/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        if (courseMapper.selectById(id) == null) {
            throw new BusinessException("课程不存在");
        }
        courseController.checkCourseDeletable(id);
        courseMapper.deleteById(id);
        return Result.ok();
    }

    // ==================== 帖子审核 ====================

    /**
     * 帖子分页（status: null全部 / 0待审核 / 1已发布 / 2已屏蔽）
     */
    @GetMapping("/post/page")
    public Result<IPage<Map<String, Object>>> postPage(@RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size,
                                                       @RequestParam(required = false) Integer status,
                                                       @RequestParam(required = false) String keyword) {
        return Result.ok(postMapper.pageForAdmin(
                new Page<>(page, size), status,
                StringUtils.hasText(keyword) ? keyword.trim() : null));
    }

    /**
     * 审核操作：0待审核 / 1通过发布 / 2屏蔽
     */
    @PutMapping("/post/{id}/status/{status}")
    public Result<Void> auditPost(@PathVariable Long id, @PathVariable Integer status) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        if (status == null || status < 0 || status > 2) {
            throw new BusinessException("非法的审核状态");
        }
        post.setStatus(status);
        postMapper.updateById(post);
        return Result.ok();
    }

    /**
     * 删除帖子（级联删除回复、点赞、收藏）
     */
    @DeleteMapping("/post/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        if (postMapper.selectById(id) == null) {
            throw new BusinessException("帖子不存在");
        }
        postController.deletePostCascade(id);
        return Result.ok();
    }

    // ==================== 用户管理 ====================

    @GetMapping("/user/page")
    public Result<IPage<User>> userPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(User::getUsername, keyword.trim())
                    .or().like(User::getStudentNo, keyword.trim()));
        }
        qw.orderByDesc(User::getCreateTime);

        IPage<User> result = userMapper.selectPage(new Page<>(page, size), qw);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.ok(result);
    }

    /**
     * 修改用户状态：0正常 / 1禁言 / 2封禁
     */
    @PutMapping("/user/{id}/status/{status}")
    public Result<Void> changeUserStatus(@PathVariable Long id, @PathVariable Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getRole() == User.ROLE_ADMIN) {
            throw new BusinessException("不能对管理员账号进行该操作");
        }
        if (status == null || status < 0 || status > 2) {
            throw new BusinessException("非法的用户状态");
        }
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.ok();
    }

    /**
     * 重置用户密码为 123456
     */
    @PutMapping("/user/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getRole() == User.ROLE_ADMIN) {
            throw new BusinessException("不能重置管理员密码");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(
                (DEFAULT_PASSWORD + "campus").getBytes(StandardCharsets.UTF_8)));
        userMapper.updateById(user);
        return Result.ok();
    }
}
