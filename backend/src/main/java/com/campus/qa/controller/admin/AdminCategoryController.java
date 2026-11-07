package com.campus.qa.controller.admin;

import com.campus.qa.dto.Result;
import com.campus.qa.entity.Category;
import com.campus.qa.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员 - 课程分类管理接口 —— 对应需求 4.2.1 课程分类管理功能
 */
@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    /** 分类列表：GET /api/admin/category/list */
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.ok(categoryService.listAll());
    }

    /** 新增分类：POST /api/admin/category */
    @PostMapping("")
    public Result<Void> add(@RequestBody Category category) {
        categoryService.add(category);
        return Result.ok("新增成功", null);
    }

    /** 修改分类：PUT /api/admin/category */
    @PutMapping("")
    public Result<Void> update(@RequestBody Category category) {
        categoryService.update(category);
        return Result.ok("修改成功", null);
    }

    /** 删除分类：DELETE /api/admin/category/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok("删除成功", null);
    }
}
