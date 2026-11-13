package com.campus.qa.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.qa.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    /**
     * 我的收藏列表（带帖子信息）
     */
    @Select("SELECT f.id, f.create_time AS createTime, p.id AS postId, p.title, " +
            "       p.like_count AS likeCount, p.status, c.name AS courseName " +
            "FROM favorite f JOIN post p ON p.id = f.post_id JOIN course c ON c.id = p.course_id " +
            "WHERE f.user_id = #{userId} ORDER BY f.create_time DESC")
    List<Map<String, Object>> listFavorites(@Param("userId") Long userId);
}
