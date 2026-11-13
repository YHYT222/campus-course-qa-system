package com.campus.qa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
@TableName("`user`")
public class User {

    public static final int ROLE_STUDENT = 0;
    public static final int ROLE_ADMIN = 1;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_MUTED = 1;
    public static final int STATUS_BANNED = 2;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String studentNo;

    private String username;

    private String password;

    private Integer role;

    private String avatar;

    private Integer status;

    private LocalDateTime createTime;
}
