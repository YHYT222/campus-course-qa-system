package com.campus.qa.entity;

import java.util.Date;

/**
 * 答疑帖子实体类 —— 对应数据表 t_post（需求 7.3）
 * 字段：id、用户id、课程id、问题内容、图片、点赞数、发布时间、帖子状态
 * 帖子状态：0-待审核 1-已通过 2-已屏蔽
 */
public class Post {

    /** 主键ID */
    private Long id;

    /** 发布用户id */
    private Long userId;

    /** 所属课程分类id */
    private Long categoryId;

    /** 问题内容 */
    private String content;

    /** 问题配图访问路径 */
    private String image;

    /** 点赞数 */
    private Integer likeCount;

    /** 发布时间 */
    private Date createTime;

    /** 帖子状态：0-待审核 1-已通过 2-已屏蔽 */
    private Integer status;

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}