package com.campus.qa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.qa.common.BusinessException;
import com.campus.qa.common.Result;
import com.campus.qa.entity.Course;
import com.campus.qa.entity.Post;
import com.campus.qa.mapper.CourseMapper;
import com.campus.qa.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课程分类（公开接口，无需登录）
 */
@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private PostMapper postMapper;

    /**
     * 全部课程分类列表
     */
    @GetMapping("/list")
    public Result<List<Course>> list() {
        return Result.ok(courseMapper.selectList(
                new LambdaQueryWrapper<Course>().orderByAsc(Course::getId)));
    }

    /**
     * 校验：删除课程前确认该课程下无帖子（供管理端复用）
     */
    public void checkCourseDeletable(Long courseId) {
        Long count = postMapper.selectCount(new LambdaQueryWrapper<Post>().eq(Post::getCourseId, courseId));
        if (count > 0) {
            throw new BusinessException("该课程下仍有 " + count + " 个帖子，无法删除");
        }
    }

    /**
     * 校验：课程名称唯一（供管理端复用）
     */
    public void checkCourseNameUnique(Course course) {
        if (!StringUtils.hasText(course.getName())) {
            throw new BusinessException("课程名称不能为空");
        }
        LambdaQueryWrapper<Course> qw = new LambdaQueryWrapper<Course>().eq(Course::getName, course.getName().trim());
        if (course.getId() != null) {
            qw.ne(Course::getId, course.getId());
        }
        if (courseMapper.selectCount(qw) > 0) {
            throw new BusinessException("课程名称已存在");
        }
    }
}
