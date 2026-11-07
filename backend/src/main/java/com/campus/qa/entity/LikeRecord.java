package com.campus.qa.entity;

import java.util.Date;

/**
 * 点赞记录实体类 —— 对应补充表 t_like_record（支撑需求 4.1.3 点赞去重）
 */
public class LikeRecord {

    /** 主键ID */
    private Long id;

    /** 点赞用户id */
    private Long userId;

    /** 被点赞帖子id */
    private Long postId;

    /** 点赞时间 */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}