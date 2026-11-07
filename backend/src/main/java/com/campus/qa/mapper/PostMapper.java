package com.campus.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.qa.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 分页查询已发布帖子（学生端首页），带作者昵称、课程名、回复数
     */
    @Select("<script>" +
            "SELECT p.id, p.user_id AS userId, p.course_id AS courseId, p.title, p.content, p.image, " +
            "       p.like_count AS likeCount, p.status, p.create_time AS createTime, " +
            "       u.username AS authorName, u.avatar AS authorAvatar, c.name AS courseName, " +
            "       (SELECT COUNT(*) FROM reply r WHERE r.post_id = p.id) AS replyCount " +
            "FROM post p JOIN `user` u ON u.id = p.user_id JOIN course c ON c.id = p.course_id " +
            "WHERE p.status = 1 " +
            "<if test='courseId != null'>AND p.course_id = #{courseId} </if>" +
            "<if test=\"keyword != null and keyword != ''\">AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR p.content LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "ORDER BY p.create_time DESC" +
            "</script>")
    IPage<Map<String, Object>> pagePublished(Page<?> page,
                                             @Param("courseId") Long courseId,
                                             @Param("keyword") String keyword);

    /**
     * 帖子详情
     */
    @Select("SELECT p.id, p.user_id AS userId, p.course_id AS courseId, p.title, p.content, p.image, " +
            "       p.like_count AS likeCount, p.status, p.create_time AS createTime, " +
            "       u.username AS authorName, u.avatar AS authorAvatar, c.name AS courseName " +
            "FROM post p JOIN `user` u ON u.id = p.user_id JOIN course c ON c.id = p.course_id " +
            "WHERE p.id = #{id}")
    Map<String, Object> selectDetailById(@Param("id") Long id);

    /**
     * 我的提问（含被屏蔽状态，便于用户自查）
     */
    @Select("SELECT p.id, p.title, p.content, p.like_count AS likeCount, p.status, " +
            "       p.create_time AS createTime, c.name AS courseName, " +
            "       (SELECT COUNT(*) FROM reply r WHERE r.post_id = p.id) AS replyCount " +
            "FROM post p JOIN course c ON c.id = p.course_id " +
            "WHERE p.user_id = #{userId} ORDER BY p.create_time DESC")
    List<Map<String, Object>> listMyPosts(@Param("userId") Long userId);

    /**
     * 管理端帖子分页（可按状态/关键词筛选）
     */
    @Select("<script>" +
            "SELECT p.id, p.user_id AS userId, p.course_id AS courseId, p.title, p.content, p.image, " +
            "       p.like_count AS likeCount, p.status, p.create_time AS createTime, " +
            "       u.username AS authorName, c.name AS courseName " +
            "FROM post p JOIN `user` u ON u.id = p.user_id JOIN course c ON c.id = p.course_id " +
            "<where>" +
            "<if test='status != null'>AND p.status = #{status} </if>" +
            "<if test=\"keyword != null and keyword != ''\">AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR u.username LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "</where>" +
            "ORDER BY p.create_time DESC" +
            "</script>")
    IPage<Map<String, Object>> pageForAdmin(Page<?> page,
                                            @Param("status") Integer status,
                                            @Param("keyword") String keyword);
}
