package com.campus.qa.service.impl;

import com.campus.qa.mapper.StatsMapper;
import com.campus.qa.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计业务实现
 */
@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatsMapper statsMapper;

    @Override
    public List<Map<String, Object>> byCourse() {
        return statsMapper.countByCourse();
    }

    @Override
    public Map<String, Object> trend() {
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
            dates.add(key.substring(5)); // 只保留 MM-dd
            posts.add(postMap.getOrDefault(key, 0));
            replies.add(replyMap.getOrDefault(key, 0));
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("dates", dates);
        data.put("posts", posts);
        data.put("replies", replies);
        return data;
    }

    @Override
    public List<Map<String, Object>> topLikes() {
        return statsMapper.topLiked();
    }

    @Override
    public List<Map<String, Object>> activeUsers() {
        return statsMapper.activeUsers();
    }

    /**
     * 把 [{date, value}] 结果集转成 日期 -> 数值 的映射
     */
    private static Map<String, Integer> toDateValueMap(List<Map<String, Object>> rows) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Object d = row.get("date");
            String key;
            if (d instanceof java.sql.Date sd) {
                key = sd.toLocalDate().toString();
            } else if (d instanceof LocalDate ld) {
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
