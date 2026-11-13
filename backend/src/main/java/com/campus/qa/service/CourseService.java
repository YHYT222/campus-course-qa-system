package com.campus.qa.service;

import com.campus.qa.entity.Course;

import java.util.List;

/**
 * 课程分类业务接口
 */
public interface CourseService {

    /**
     * 全部课程分类（按 id 升序）
     */
    List<Course> listAll();

    /**
     * 新增课程分类
     */
    void add(Course course);

    /**
     * 修改课程分类
     */
    void update(Course course);

    /**
     * 删除课程分类（该课程下存在帖子时不允许删除）
     */
    void delete(Long id);

    /**
     * 校验课程名称非空且唯一
     */
    void checkNameUnique(Course course);

    /**
     * 校验课程是否可删除
     */
    void checkDeletable(Long id);
}
