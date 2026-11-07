package com.campus.qa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 数据统计专用 Mapper
 */
@Mapper
public interface StatsMapper {

    /** 各课程提问量（仅统计已发布帖子） */
    @Select("SELECT c.name AS name, COUNT(p.id) AS value " +
            "FROM course c LEFT JOIN post p ON p.course_id = c.id AND p.status = 1 " +
            "GROUP BY c.id, c.name ORDER BY value DESC")
    List<Map<String, Object>> countByCourse();

    /** 近7日提问趋势 */
    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS value FROM post " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY DATE(create_time)")
    List<Map<String, Object>> postTrend();

    /** 近7日回复趋势 */
    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS value FROM reply " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY DATE(create_time)")
    List<Map<String, Object>> replyTrend();

    /** 点赞数TOP10帖子 */
    @Select("SELECT p.id, p.title, p.like_count AS likeCount, c.name AS courseName " +
            "FROM post p JOIN course c ON c.id = p.course_id " +
            "WHERE p.status = 1 ORDER BY p.like_count DESC, p.id DESC LIMIT 10")
    List<Map<String, Object>> topLiked();

    /** 答疑最活跃用户（按回复数） */
    @Select("SELECT u.username AS name, COUNT(r.id) AS value " +
            "FROM reply r JOIN `user` u ON u.id = r.user_id " +
            "GROUP BY u.id, u.username ORDER BY value DESC LIMIT 10")
    List<Map<String, Object>> activeUsers();
}
