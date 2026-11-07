package com.campus.qa.entity;

import java.util.Date;

/**
 * 课程分类实体类 —— 对应数据表 t_category（需求 7.2）
 * 字段：id、课程名称、课程简介、创建时间
 */
public class Category {

    /** 主键ID */
    private Long id;

    /** 课程名称 */
    private String name;

    /** 课程简介 */
    private String description;

    /** 创建时间 */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}