package com.campus.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.qa.common.BusinessException;
import com.campus.qa.entity.Course;
import com.campus.qa.entity.Post;
import com.campus.qa.entity.User;
import com.campus.qa.mapper.PostMapper;
import com.campus.qa.mapper.UserMapper;
import com.campus.qa.service.AdminService;
import com.campus.qa.service.CourseService;
import com.campus.qa.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 管理员端业务实现
 */
@Service
public class AdminServiceImpl implements AdminService {

    /** 重置密码后的默认密码 */
    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private PostService postService;

    // ==================== 课程分类管理 ====================

    @Override
    public void addCourse(Course course) {
        courseService.add(course);
    }

    @Override
    public void updateCourse(Course course) {
        courseService.update(course);
    }

    @Override
    public void deleteCourse(Long id) {
        courseService.delete(id);
    }

    // ==================== 帖子审核 ====================

    @Override
    public IPage<Map<String, Object>> postPage(Integer page, Integer size, Integer status, String keyword) {
        return postMapper.pageForAdmin(new Page<>(page, size), status,
                StringUtils.hasText(keyword) ? keyword.trim() : null);
    }

    @Override
    public void auditPost(Long id, Integer status) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        if (status == null || status < Post.STATUS_PENDING || status > Post.STATUS_BLOCKED) {
            throw new BusinessException("非法的审核状态");
        }
        post.setStatus(status);
        postMapper.updateById(post);
    }

    @Override
    public void deletePost(Long id) {
        if (postMapper.selectById(id) == null) {
            throw new BusinessException("帖子不存在");
        }
        postService.deleteCascade(id);
    }

    // ==================== 用户管理 ====================

    @Override
    public IPage<User> userPage(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(User::getUsername, keyword.trim())
                    .or().like(User::getStudentNo, keyword.trim()));
        }
        qw.orderByDesc(User::getCreateTime);

        IPage<User> result = userMapper.selectPage(new Page<>(page, size), qw);
        // 返回前清除密码字段，避免泄露
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }

    @Override
    public void changeUserStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getRole() == User.ROLE_ADMIN) {
            throw new BusinessException("不能对管理员账号进行该操作");
        }
        if (status == null || status < User.STATUS_NORMAL || status > User.STATUS_BANNED) {
            throw new BusinessException("非法的用户状态");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(Long id) {
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
    }
}
