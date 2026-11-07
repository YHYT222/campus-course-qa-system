package com.campus.qa.dto;

import com.campus.qa.entity.Post;
import java.util.Date;

/**
 * 帖子展示对象：在帖子实体的基础上补充列表页 / 详情页需要的关联展示字段
 * 说明：这些字段仅用于前端展示，不写入数据库，未改动 t_post 表结构
 */
public class PostVO extends Post {

    /** 发布者用户名 */
    private String authorName;
    /** 发布者头像 */
    private String authorAvatar;
    /** 所属课程名称 */
    private String categoryName;
    /** 评论总数 */
    private Integer commentCount;
    /** 当前登录用户是否已点赞 */
    private Boolean liked;
    /** 当前登录用户是否已收藏 */
    private Boolean favorited;

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }
}
