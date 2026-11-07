package com.campus.qa.service.impl;

import com.campus.qa.dto.PageResult;
import com.campus.qa.dto.PostVO;
import com.campus.qa.entity.Favorite;
import com.campus.qa.entity.LikeRecord;
import com.campus.qa.entity.Post;
import com.campus.qa.exception.BusinessException;
import com.campus.qa.mapper.CommentMapper;
import com.campus.qa.mapper.FavoriteMapper;
import com.campus.qa.mapper.LikeRecordMapper;
import com.campus.qa.mapper.PostMapper;
import com.campus.qa.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 帖子业务层实现
 * 业务规则：帖子发布后状态为待审核（0），必须管理员审核通过（1）后才对普通学生可见
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private CommentMapper commentMapper;

    /** 帖子状态：待审核 */
    private static final int STATUS_PENDING = 0;
    /** 帖子状态：已通过 */
    private static final int STATUS_APPROVED = 1;
    /** 帖子状态：已屏蔽 */
    private static final int STATUS_BLOCKED = 2;

    @Override
    public Long publish(Long userId, Long categoryId, String content, String image) {
        // 非空校验
        if (categoryId == null) {
            throw new BusinessException("请选择课程分类");
        }
        if (content == null || content.trim().length() == 0) {
            throw new BusinessException("问题内容不能为空");
        }
        if (content.trim().length() > 2000) {
            throw new BusinessException("问题内容过长，请控制在 2000 字以内");
        }
        // 简单非法字符过滤
        String lower = content.toLowerCase();
        if (lower.contains("<script") || lower.contains("javascript:")) {
            throw new BusinessException("问题内容包含非法字符，请修改后重新提交");
        }
        Post post = new Post();
        post.setUserId(userId);
        post.setCategoryId(categoryId);
        post.setContent(content.trim());
        post.setImage(image);
        // 新发布帖子一律为待审核状态，需管理员审核后才公开
        post.setStatus(STATUS_PENDING);
        postMapper.insert(post);
        return post.getId();
    }

    @Override
    public PageResult<PostVO> page(Integer pageNum, Integer pageSize, Long categoryId,
                                   String keyword, Long currentUserId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        int offset = (pageNum - 1) * pageSize;
        List<PostVO> list = postMapper.selectApprovedPage(categoryId, keyword, offset, pageSize);
        Long total = postMapper.countApproved(categoryId, keyword);
        // 填充当前用户的点赞 / 收藏状态，供前端高亮按钮
        fillUserFlag(list, currentUserId);
        return new PageResult<PostVO>(list, total, pageNum, pageSize);
    }

    @Override
    public PostVO detail(Long id, Long currentUserId) {
        PostVO vo = postMapper.selectVOById(id);
        if (vo == null) {
            throw new BusinessException("帖子不存在或已被删除");
        }
        // 未通过审核的帖子不对外展示
        if (!STATUS_APPROVED.equals(vo.getStatus())) {
            throw new BusinessException("该帖子尚未通过审核，暂不可查看");
        }
        List<PostVO> single = new ArrayList<PostVO>();
        single.add(vo);
        fillUserFlag(single, currentUserId);
        return vo;
    }

    @Override
    public List<PostVO> myPosts(Long userId) {
        List<PostVO> list = postMapper.selectByUserId(userId);
        fillUserFlag(list, userId);
        return list;
    }

    @Override
    public void delete(Long id, Long userId, boolean isAdmin) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException("帖子不存在或已被删除");
        }
        // 权限校验：非管理员只能删除自己发布的帖子
        if (!isAdmin && !post.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己发布的帖子");
        }
        // 级联清理该帖子关联的评论、收藏、点赞记录
        commentMapper.deleteByPostId(id);
        favoriteMapper.deleteByPostId(id);
        likeRecordMapper.deleteByPostId(id);
        postMapper.deleteById(id);
    }

    @Override
    public void like(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        // 已点赞则不再重复计数
        if (likeRecordMapper.selectByUserAndPost(userId, postId) != null) {
            return;
        }
        LikeRecord record = new LikeRecord();
        record.setUserId(userId);
        record.setPostId(postId);
        likeRecordMapper.insert(record);
        postMapper.incrLikeCount(postId);
    }

    @Override
    public void unlike(Long postId, Long userId) {
        // 未点赞记录则不处理，保证点赞数不为负
        if (likeRecordMapper.selectByUserAndPost(userId, postId) == null) {
            return;
        }
        likeRecordMapper.delete(userId, postId);
        postMapper.decrLikeCount(postId);
    }

    @Override
    public void favorite(Long postId, Long userId) {
        if (postMapper.selectById(postId) == null) {
            throw new BusinessException("帖子不存在");
        }
        // 已收藏则不重复插入
        if (favoriteMapper.selectByUserAndPost(userId, postId) != null) {
            return;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setPostId(postId);
        favoriteMapper.insert(favorite);
    }

    @Override
    public void unfavorite(Long postId, Long userId) {
        favoriteMapper.delete(userId, postId);
    }

    @Override
    public List<PostVO> favorites(Long userId) {
        List<PostVO> list = postMapper.selectFavorites(userId);
        // 收藏列表默认标记为已收藏
        for (PostVO vo : list) {
            vo.setFavorited(true);
        }
        return list;
    }

    @Override
    public PageResult<PostVO> adminPage(Integer pageNum, Integer pageSize, Integer status, String keyword) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        int offset = (pageNum - 1) * pageSize;
        List<PostVO> list = postMapper.selectAdminPage(status, keyword, offset, pageSize);
        Long total = postMapper.countAdmin(status, keyword);
        return new PageResult<PostVO>(list, total, pageNum, pageSize);
    }

    @Override
    public void audit(Long id, Integer status) {
        if (status == null || (!status.equals(STATUS_APPROVED) && !status.equals(STATUS_BLOCKED))) {
            throw new BusinessException("审核状态不合法，仅支持 1-通过 或 2-屏蔽");
        }
        if (postMapper.selectById(id) == null) {
            throw new BusinessException("帖子不存在");
        }
        postMapper.updateStatus(id, status);
    }

    /** 批量填充当前用户的点赞与收藏状态 */
    private void fillUserFlag(List<PostVO> list, Long currentUserId) {
        if (list == null || list.isEmpty() || currentUserId == null) {
            return;
        }
        for (PostVO vo : list) {
            vo.setLiked(likeRecordMapper.selectByUserAndPost(currentUserId, vo.getId()) != null);
            vo.setFavorited(favoriteMapper.selectByUserAndPost(currentUserId, vo.getId()) != null);
        }
    }
}
