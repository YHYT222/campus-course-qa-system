package com.campus.qa.controller.admin;

import com.campus.qa.dto.Result;
import com.campus.qa.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 管理员 - 数据可视化统计接口 —— 对应需求 4.2.4 数据可视化统计功能
 */
@RestController
@RequestMapping("/api/admin/stat")
public class AdminStatController {

    @Autowired
    private StatService statService;

    /** 各课程分类提问数量（柱状图）：GET /api/admin/stat/category */
    @GetMapping("/category")
    public Result<List<Map<String, Object>>> category() {
        return Result.ok(statService.categoryStat());
    }

    /** 近 N 日平台答疑活跃度（折线图）：GET /api/admin/stat/activity?days=7 */
    @GetMapping("/activity")
    public Result<List<Map<String, Object>>> activity(@RequestParam(required = false) Integer days) {
        return Result.ok(statService.activityStat(days));
    }

    /** 平台运营总览：GET /api/admin/stat/overview */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.ok(statService.overview());
    }
}
