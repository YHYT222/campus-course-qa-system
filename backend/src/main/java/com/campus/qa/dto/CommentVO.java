package com.campus.qa.dto;

import com.campus.qa.entity.Comment;

/**
 * 评论展示对象：在评论实体基础上补充回复者信息，供前端展示
 */
public class CommentVO extends Comment {

    /** 回复者用户名 */
    private String username;
    /** 回复者头像 */
    private String avatar;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
