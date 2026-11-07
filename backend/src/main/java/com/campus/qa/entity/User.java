package com.campus.qa.entity;

import java.util.Date;

/**
 * 用户实体类 —— 对应数据表 t_user（需求 7.1）
 * 字段：id、学号、用户名、密码、角色（学生/管理员）、头像、注册时间
 */
public class User {

    /** 主键ID */
    private Long id;

    /** 学号 */
    private String studentNo;

    /** 用户名 */
    private String username;

    /** 密码（MD5密文） */
    private String password;

    /** 角色：student-学生 / admin-管理员 */
    private String role;

    /** 头像访问路径 */
    private String avatar;

    /** 注册时间 */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}