package com.campus.qa.entity;

import java.util.Date;

/**
 * 回复评论实体类 —— 对应数据表 t_comment（需求 7.4）
 * 字段：id、帖子id、回复用户id、评论内容、回复时间
 */
public class Comment {

    /** 主键ID */
    private Long id;

    /** 所属帖子id */
    private Long postId;

    /** 回复用户id */
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 回复时间 */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}