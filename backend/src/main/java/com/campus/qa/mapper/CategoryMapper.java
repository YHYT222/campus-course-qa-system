package com.campus.qa.mapper;

import com.campus.qa.entity.Category;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 课程分类数据访问层 —— 操作 t_category 表
 */
public interface CategoryMapper {

    /** 查询全部课程分类（按创建时间倒序） */
    List<Category> selectAll();

    /** 根据主键查询 */
    Category selectById(@Param("id") Long id);

    /** 新增分类 */
    int insert(Category category);

    /** 修改分类 */
    int update(Category category);

    /** 删除分类 */
    int deleteById(@Param("id") Long id);

    /** 统计该分类下的帖子数量（删除分类时用于提示） */
    Long countPost(@Param("id") Long id);
}
