package com.campus.qa.service;

import com.campus.qa.entity.Category;
import java.util.List;

/**
 * 课程分类业务层接口 —— 对应需求 4.2.1 课程分类管理
 */
public interface CategoryService {

    /** 查询全部课程分类 */
    List<Category> listAll();

    /** 根据id查询 */
    Category getById(Long id);

    /** 新增分类 */
    void add(Category category);

    /** 修改分类 */
    void update(Category category);

    /** 删除分类 */
    void delete(Long id);
}
