package com.campus.qa.service.impl;

import com.campus.qa.entity.Category;
import com.campus.qa.exception.BusinessException;
import com.campus.qa.mapper.CategoryMapper;
import com.campus.qa.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程分类业务层实现
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> listAll() {
        return categoryMapper.selectAll();
    }

    @Override
    public Category getById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public void add(Category category) {
        if (category == null || category.getName() == null || category.getName().trim().length() == 0) {
            throw new BusinessException("课程名称不能为空");
        }
        categoryMapper.insert(category);
    }

    @Override
    public void update(Category category) {
        if (category == null || category.getId() == null) {
            throw new BusinessException("参数不完整，无法修改");
        }
        if (category.getName() == null || category.getName().trim().length() == 0) {
            throw new BusinessException("课程名称不能为空");
        }
        if (categoryMapper.selectById(category.getId()) == null) {
            throw new BusinessException("该课程分类不存在");
        }
        categoryMapper.update(category);
    }

    @Override
    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException("该课程分类不存在");
        }
        // 该分类下已有帖子时给出提示，避免帖子失去归属
        Long postCount = categoryMapper.countPost(id);
        if (postCount != null && postCount > 0) {
            throw new BusinessException("该分类下仍有 " + postCount + " 条帖子，请先处理后再删除");
        }
        categoryMapper.deleteById(id);
    }
}
