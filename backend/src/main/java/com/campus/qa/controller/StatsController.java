package com.campus.qa.controller;

import com.campus.qa.common.Result;
import com.campus.qa.mapper.StatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计（管理员端可视化）
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private StatsMapper statsMapper;

    /**
     * 各课程提问量（柱状图）
     */
    @GetMapping("/course")
    public Result<List<Map<String, Object>>> byCourse() {
        return Result.ok(statsMapper.countByCourse());
    }

    /**
     * 近7日提问/回复趋势（折线图，缺失日期补0）
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> trend() {
        List<Map<String, Object>> postRows = statsMapper.postTrend();
        List<Map<String, Object>> replyRows = statsMapper.replyTrend();

        Map<String, Integer> postMap = toDateValueMap(postRows);
        Map<String, Integer> replyMap = toDateValueMap(replyRows);

        List<String> dates = new ArrayList<>();
        List<Integer> posts = new ArrayList<>();
        List<Integer> replies = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            String key = today.minusDays(i).toString();
            dates.add(key.substring(5)); // MM-dd
            posts.add(postMap.getOrDefault(key, 0));
            replies.add(replyMap.getOrDefault(key, 0));
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("dates", dates);
        data.put("posts", posts);
        data.put("replies", replies);
        return Result.ok(data);
    }

    /**
     * 点赞TOP10帖子（横向柱状图）
     */
    @GetMapping("/top-likes")
    public Result<List<Map<String, Object>>> topLikes() {
        return Result.ok(statsMapper.topLiked());
    }

    /**
     * 答疑活跃用户TOP10（按回复数）
     */
    @GetMapping("/active-users")
    public Result<List<Map<String, Object>>> activeUsers() {
        return Result.ok(statsMapper.activeUsers());
    }

    private static Map<String, Integer> toDateValueMap(List<Map<String, Object>> rows) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Object d = row.get("date");
            String key;
            if (d instanceof java.sql.Date sd) {
                key = sd.toLocalDate().toString();
            } else if (d instanceof java.time.LocalDate ld) {
                key = ld.toString();
            } else {
                key = String.valueOf(d).substring(0, 10);
            }
            Object v = row.get("value");
            map.put(key, v instanceof Number n ? n.intValue() : 0);
        }
        return map;
    }
}
