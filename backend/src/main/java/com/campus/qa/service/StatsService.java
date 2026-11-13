package com.campus.qa.service;

import java.util.List;
import java.util.Map;

/**
 * 数据统计业务接口（管理员端可视化）
 */
public interface StatsService {

    /**
     * 各课程提问量
     */
    List<Map<String, Object>> byCourse();

    /**
     * 近 7 日提问 / 回复趋势（缺失日期补 0）
     */
    Map<String, Object> trend();

    /**
     * 点赞数 TOP10 帖子
     */
    List<Map<String, Object>> topLikes();

    /**
     * 答疑活跃用户 TOP10
     */
    List<Map<String, Object>> activeUsers();
}
