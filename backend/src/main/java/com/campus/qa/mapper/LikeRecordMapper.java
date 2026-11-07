package com.campus.qa.mapper;

import com.campus.qa.entity.LikeRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 点赞记录数据访问层 —— 操作补充表 t_like_record
 * 该表用于防止同一用户重复点赞，支撑需求 4.1.3 的点赞与取消点赞
 */
public interface LikeRecordMapper {

    /** 查询某用户对某帖子的点赞记录 */
    LikeRecord selectByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);

    /** 新增点赞记录 */
    int insert(LikeRecord record);

    /** 删除点赞记录 */
    int delete(@Param("userId") Long userId, @Param("postId") Long postId);

    /** 删除某帖子的全部点赞记录（删帖时级联清理） */
    int deleteByPostId(@Param("postId") Long postId);
}
