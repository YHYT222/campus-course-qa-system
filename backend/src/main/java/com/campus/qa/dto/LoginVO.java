package com.campus.qa.dto;

/**
 * 登录成功后的返回对象：包含登录令牌与用户基本信息
 */
public class LoginVO {

    /** 登录令牌，后续请求需在请求头 token 中携带 */
    private String token;
    /** 用户id */
    private Long id;
    /** 学号 */
    private String studentNo;
    /** 用户名 */
    private String username;
    /** 角色：student / admin */
    private String role;
    /** 头像访问路径 */
    private String avatar;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

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
}
