package com.campus.qa.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

/**
 * 答疑帖子业务接口
 */
public interface PostService {

    /**
     * 分页浏览已发布帖子（支持课程筛选 + 关键词搜索）
     */
    IPage<Map<String, Object>> page(Integer page, Integer size, Long courseId, String keyword);

    /**
     * 帖子详情（含回复列表、当前用户点赞/收藏状态）
     */
    Map<String, Object> detail(Long id);

    /**
     * 发布帖子：校验通过后置为「待审核」状态
     */
    void publish(Map<String, Object> body);

    /**
     * 我的提问
     */
    List<Map<String, Object>> myPosts();

    /**
     * 删除自己的帖子（管理员可删任意帖子）
     */
    void deleteMyPost(Long id);

    /**
     * 级联删除帖子及其回复、点赞、收藏记录
     */
    void deleteCascade(Long postId);
}
