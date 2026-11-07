package com.campus.qa.service.impl;

import com.campus.qa.mapper.CommentMapper;
import com.campus.qa.mapper.PostMapper;
import com.campus.qa.mapper.UserMapper;
import com.campus.qa.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计业务层实现
 */
@Service
public class StatServiceImpl implements StatService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Map<String, Object>> categoryStat() {
        // 直接复用 Mapper 中按课程分组统计的结果
        return postMapper.categoryStat();
    }

    @Override
    public List<Map<String, Object>> activityStat(Integer days) {
        if (days == null || days < 1) {
            days = 7;
        }
        return postMapper.activityStat(days);
    }

    @Override
    public Map<String, Object> overview() {
        Map<String, Object> map = new HashMap<String, Object>();
        Long userCount = userMapper.countAll();
        Long postCount = postMapper.countByStatus(null);
        Long pendingCount = postMapper.countByStatus(0);
        Long approvedCount = postMapper.countByStatus(1);
        Long commentCount = commentMapper.countAll();
        map.put("userCount", userCount == null ? 0 : userCount);
        map.put("postCount", postCount == null ? 0 : postCount);
        map.put("pendingCount", pendingCount == null ? 0 : pendingCount);
        map.put("approvedCount", approvedCount == null ? 0 : approvedCount);
        map.put("commentCount", commentCount == null ? 0 : commentCount);
        return map;
    }
}
