package com.campus.qa.mapper;

import com.campus.qa.dto.PostVO;
import com.campus.qa.entity.Post;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 答疑帖子数据访问层 —— 操作 t_post 表
 */
public interface PostMapper {

    /** 新增帖子 */
    int insert(Post post);

    /** 根据主键查询帖子原始记录 */
    Post selectById(@Param("id") Long id);

    /** 根据主键查询帖子展示对象（带作者名、课程名、评论数） */
    PostVO selectVOById(@Param("id") Long id);

    /** 修改帖子的审核状态 */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 点赞数 +1 */
    int incrLikeCount(@Param("id") Long id);

    /** 点赞数 -1（不小于 0） */
    int decrLikeCount(@Param("id") Long id);

    /** 删除帖子 */
    int deleteById(@Param("id") Long id);

    /** 前台：分页查询已审核通过的帖子，支持课程筛选与关键词模糊搜索 */
    List<PostVO> selectApprovedPage(@Param("categoryId") Long categoryId,
                                    @Param("keyword") String keyword,
                                    @Param("offset") Integer offset,
                                    @Param("pageSize") Integer pageSize);

    /** 前台：统计已通过帖子总数 */
    Long countApproved(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    /** 个人中心：查询指定用户发布的全部帖子 */
    List<PostVO> selectByUserId(@Param("userId") Long userId);

    /** 个人中心：查询指定用户收藏的帖子 */
    List<PostVO> selectFavorites(@Param("userId") Long userId);

    /** 管理员：分页查询全部帖子（可按状态筛选、关键词搜索） */
    List<PostVO> selectAdminPage(@Param("status") Integer status,
                                 @Param("keyword") String keyword,
                                 @Param("offset") Integer offset,
                                 @Param("pageSize") Integer pageSize);

    /** 管理员：统计帖子总数 */
    Long countAdmin(@Param("status") Integer status, @Param("keyword") String keyword);

    /** 统计帖子总数（按状态可选） */
    Long countByStatus(@Param("status") Integer status);

    /** 统计各课程分类下的提问数量（柱状图数据：课程名 + 帖子数） */
    List<Map<String, Object>> categoryStat();

    /** 统计近 N 日平台答疑活跃度（折线图数据：日期 + 帖子与评论总数） */
    List<Map<String, Object>> activityStat(@Param("days") Integer days);
}
