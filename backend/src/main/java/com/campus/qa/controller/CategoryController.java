package com.campus.qa.controller;

import com.campus.qa.dto.Result;
import com.campus.qa.entity.Category;
import com.campus.qa.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课程分类接口（学生侧）：发帖选分类、首页筛选课程时使用
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /** 查询全部课程分类：GET /api/category/list（无需登录） */
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(categoryService.listAll());
    }
}
