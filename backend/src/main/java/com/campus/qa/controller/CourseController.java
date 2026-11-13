package com.campus.qa.controller;

import com.campus.qa.common.Result;
import com.campus.qa.entity.Course;
import com.campus.qa.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private CourseService courseService;

    /**
     * 全部课程分类列表
     */
    @GetMapping("/list")
    public Result<List<Course>> list() {
        return Result.ok(courseService.listAll());
    }
}
