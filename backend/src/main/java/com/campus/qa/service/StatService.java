package com.campus.qa.service;

import java.util.List;
import java.util.Map;

/**
 * 数据统计业务层接口 —— 对应需求 4.2.4 数据可视化统计
 */
public interface StatService {

    /** 各课程分类提问数量统计（柱状图数据） */
    List<Map<String, Object>> categoryStat();

    /** 近 7 日平台答疑活跃度统计（折线图数据） */
    List<Map<String, Object>> activityStat(Integer days);

    /** 平台运营总览数据 */
    Map<String, Object> overview();
}
