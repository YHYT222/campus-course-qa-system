package com.campus.qa.service;

import java.util.List;
import java.util.Map;

/**
 * 互动业务接口：点赞、收藏、回复
 */
public interface InteractionService {

    /**
     * 点赞 / 取消点赞
     */
    Map<String, Object> toggleLike(Long postId);

    /**
     * 收藏 / 取消收藏
     */
    Map<String, Object> toggleFavorite(Long postId);

    /**
     * 我的收藏列表
     */
    List<Map<String, Object>> favorites();

    /**
     * 发表回复
     */
    void addReply(Map<String, String> body);

    /**
     * 某帖子的回复列表
     */
    List<Map<String, Object>> replies(Long postId);

    /**
     * 我的回复列表
     */
    List<Map<String, Object>> myReplies();

    /**
     * 删除自己的回复
     */
    void deleteReply(Long replyId);
}
