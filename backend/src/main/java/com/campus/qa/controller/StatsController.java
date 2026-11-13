package com.campus.qa.controller;

import com.campus.qa.common.Result;
import com.campus.qa.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 数据统计（管理员端可视化）
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    /**
     * 各课程提问量（柱状图）
     */
    @GetMapping("/course")
    public Result<List<Map<String, Object>>> byCourse() {
        return Result.ok(statsService.byCourse());
    }

    /**
     * 近 7 日提问 / 回复趋势（折线图，缺失日期补 0）
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> trend() {
        return Result.ok(statsService.trend());
    }

    /**
     * 点赞 TOP10 帖子（横向柱状图）
     */
    @GetMapping("/top-likes")
    public Result<List<Map<String, Object>>> topLikes() {
        return Result.ok(statsService.topLikes());
    }

    /**
     * 答疑活跃用户 TOP10（按回复数）
     */
    @GetMapping("/active-users")
    public Result<List<Map<String, Object>>> activeUsers() {
        return Result.ok(statsService.activeUsers());
    }
}
