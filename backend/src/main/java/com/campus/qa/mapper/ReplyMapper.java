package com.campus.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.qa.entity.Reply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReplyMapper extends BaseMapper<Reply> {

    /**
     * 某帖子的全部回复（带回复人信息）
     */
    @Select("SELECT r.id, r.post_id AS postId, r.user_id AS userId, r.content, " +
            "       r.create_time AS createTime, u.username AS authorName, u.avatar AS authorAvatar " +
            "FROM reply r JOIN `user` u ON u.id = r.user_id " +
            "WHERE r.post_id = #{postId} ORDER BY r.create_time ASC")
    List<Map<String, Object>> listByPost(@Param("postId") Long postId);

    /**
     * 我的回复（带帖子标题）
     */
    @Select("SELECT r.id, r.content, r.create_time AS createTime, r.post_id AS postId, p.title AS postTitle " +
            "FROM reply r JOIN post p ON p.id = r.post_id " +
            "WHERE r.user_id = #{userId} ORDER BY r.create_time DESC")
    List<Map<String, Object>> listMyReplies(@Param("userId") Long userId);
}
