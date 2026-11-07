package com.campus.qa.service;

import com.campus.qa.dto.PageResult;
import com.campus.qa.dto.PostVO;
import java.util.List;

/**
 * 帖子业务层接口 —— 对应需求 4.1.2 发帖、4.1.3 问答交互、4.2.2 帖子审核
 */
public interface PostService {

    /** 发布帖子（发布后状态为待审核） */
    Long publish(Long userId, Long categoryId, String content, String image);

    /** 前台分页查询已通过帖子 */
    PageResult<PostVO> page(Integer pageNum, Integer pageSize, Long categoryId, String keyword, Long currentUserId);

    /** 帖子详情 */
    PostVO detail(Long id, Long currentUserId);

    /** 我的提问 */
    List<PostVO> myPosts(Long userId);

    /** 删除帖子：学生仅可删除自己的，管理员可删除任意帖子 */
    void delete(Long id, Long userId, boolean isAdmin);

    /** 点赞 */
    void like(Long postId, Long userId);

    /** 取消点赞 */
    void unlike(Long postId, Long userId);

    /** 收藏 */
    void favorite(Long postId, Long userId);

    /** 取消收藏 */
    void unfavorite(Long postId, Long userId);

    /** 我的收藏 */
    List<PostVO> favorites(Long userId);

    /** 管理员：分页查询全部帖子 */
    PageResult<PostVO> adminPage(Integer pageNum, Integer pageSize, Integer status, String keyword);

    /** 管理员：审核帖子（1-通过 2-屏蔽） */
    void audit(Long id, Integer status);
}
