package com.campus.qa.mapper;

import com.campus.qa.entity.Favorite;
import org.apache.ibatis.annotations.Param;

/**
 * 收藏数据访问层 —— 操作补充表 t_favorite
 * 该表仅用于支撑需求 4.1.3 的收藏与取消收藏，未改动需求文档规定的 4 张核心表
 */
public interface FavoriteMapper {

    /** 查询某用户对某帖子的收藏记录 */
    Favorite selectByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);

    /** 新增收藏 */
    int insert(Favorite favorite);

    /** 取消收藏 */
    int delete(@Param("userId") Long userId, @Param("postId") Long postId);

    /** 删除某帖子的全部收藏记录（删帖时级联清理） */
    int deleteByPostId(@Param("postId") Long postId);
}
