package com.campus.qa.service;

import com.campus.qa.dto.CommentVO;
import java.util.List;

/**
 * 评论业务层接口 —— 对应需求 4.1.3 问答交互、4.1.4 个人中心
 */
public interface CommentService {

    /** 发表评论 */
    Long add(Long postId, Long userId, String content);

    /** 查询某帖子的全部评论 */
    List<CommentVO> listByPost(Long postId);

    /** 我的回复 */
    List<CommentVO> myComments(Long userId);

    /** 删除评论：学生仅可删除自己的，管理员可删除任意评论 */
    void delete(Long id, Long userId, boolean isAdmin);

    /** 管理员：查询全部评论 */
    List<CommentVO> listAll(String keyword);

    /** 统计评论总数 */
    Long countAll();
}
