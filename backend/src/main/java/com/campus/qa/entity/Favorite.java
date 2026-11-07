package com.campus.qa.entity;

import java.util.Date;

/**
 * 收藏实体类 —— 对应补充表 t_favorite（支撑需求 4.1.3 收藏功能）
 */
public class Favorite {

    /** 主键ID */
    private Long id;

    /** 收藏用户id */
    private Long userId;

    /** 被收藏帖子id */
    private Long postId;

    /** 收藏时间 */
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