package com.campus.qa.mapper;

import com.campus.qa.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户数据访问层 —— 操作 t_user 表
 */
public interface UserMapper {

    /** 根据主键查询用户 */
    User selectById(@Param("id") Long id);

    /** 根据学号查询用户（注册查重、登录使用） */
    User selectByStudentNo(@Param("studentNo") String studentNo);

    /** 根据用户名查询用户 */
    User selectByUsername(@Param("username") String username);

    /** 管理员：按学号或用户名关键字模糊查询用户列表 */
    List<User> selectByKeyword(@Param("keyword") String keyword);

    /** 查询全部用户 */
    List<User> selectAll();

    /** 新增用户 */
    int insert(User user);

    /** 修改用户信息 */
    int update(User user);

    /** 删除用户 */
    int deleteById(@Param("id") Long id);

    /** 统计用户总数 */
    Long countAll();
}
