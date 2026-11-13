package com.campus.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.qa.common.BusinessException;
import com.campus.qa.entity.Course;
import com.campus.qa.entity.Post;
import com.campus.qa.mapper.CourseMapper;
import com.campus.qa.mapper.PostMapper;
import com.campus.qa.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 课程分类业务实现
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public List<Course> listAll() {
        return courseMapper.selectList(new LambdaQueryWrapper<Course>().orderByAsc(Course::getId));
    }

    @Override
    public void add(Course course) {
        checkNameUnique(course);
        course.setId(null);
        course.setName(course.getName().trim());
        if (course.getDescription() == null) {
            course.setDescription("");
        }
        courseMapper.insert(course);
    }

    @Override
    public void update(Course course) {
        if (course.getId() == null || courseMapper.selectById(course.getId()) == null) {
            throw new BusinessException("课程不存在");
        }
        checkNameUnique(course);
        course.setName(course.getName().trim());
        courseMapper.updateById(course);
    }

    @Override
    public void delete(Long id) {
        if (courseMapper.selectById(id) == null) {
            throw new BusinessException("课程不存在");
        }
        checkDeletable(id);
        courseMapper.deleteById(id);
    }

    @Override
    public void checkNameUnique(Course course) {
        if (course == null || !StringUtils.hasText(course.getName())) {
            throw new BusinessException("课程名称不能为空");
        }
        LambdaQueryWrapper<Course> qw = new LambdaQueryWrapper<Course>()
                .eq(Course::getName, course.getName().trim());
        if (course.getId() != null) {
            qw.ne(Course::getId, course.getId());
        }
        if (courseMapper.selectCount(qw) > 0) {
            throw new BusinessException("课程名称已存在");
        }
    }

    @Override
    public void checkDeletable(Long id) {
        Long count = postMapper.selectCount(
                new LambdaQueryWrapper<Post>().eq(Post::getCourseId, id));
        if (count > 0) {
            throw new BusinessException("该课程下仍有 " + count + " 个帖子，无法删除");
        }
    }
}
