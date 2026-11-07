package com.campus.qa.mapper;

import com.campus.qa.dto.CommentVO;
import com.campus.qa.entity.Comment;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 回复评论数据访问层 —— 操作 t_comment 表
 */
public interface CommentMapper {

    /** 新增评论 */
    int insert(Comment comment);

    /** 根据主键查询评论原始记录 */
    Comment selectById(@Param("id") Long id);

    /** 查询某帖子的全部评论（带回复者信息） */
    List<CommentVO> selectVOByPostId(@Param("postId") Long postId);

    /** 查询某用户发布的全部评论（个人中心：我的回复） */
    List<CommentVO> selectVOByUserId(@Param("userId") Long userId);

    /** 管理员：查询全部评论 */
    List<CommentVO> selectVOAll(@Param("keyword") String keyword);

    /** 删除评论 */
    int deleteById(@Param("id") Long id);

    /** 删除某帖子下的全部评论（删帖时级联清理） */
    int deleteByPostId(@Param("postId") Long postId);

    /** 统计评论总数 */
    Long countAll();
}
