package com.campus.qa.service.impl;

import com.campus.qa.dto.CommentVO;
import com.campus.qa.entity.Comment;
import com.campus.qa.entity.Post;
import com.campus.qa.exception.BusinessException;
import com.campus.qa.mapper.CommentMapper;
import com.campus.qa.mapper.PostMapper;
import com.campus.qa.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论业务层实现
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public Long add(Long postId, Long userId, String content) {
        if (postId == null) {
            throw new BusinessException("帖子id不能为空");
        }
        if (content == null || content.trim().length() == 0) {
            throw new BusinessException("评论内容不能为空");
        }
        if (content.trim().length() > 1000) {
            throw new BusinessException("评论内容过长，请控制在 1000 字以内");
        }
        String lower = content.toLowerCase();
        if (lower.contains("<script") || lower.contains("javascript:")) {
            throw new BusinessException("评论内容包含非法字符，请修改后重新提交");
        }
        // 仅允许评论已审核通过的帖子
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        if (post.getStatus() == null || post.getStatus() != 1) {
            throw new BusinessException("该帖子尚未通过审核，暂不可评论");
        }
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content.trim());
        commentMapper.insert(comment);
        return comment.getId();
    }

    @Override
    public List<CommentVO> listByPost(Long postId) {
        return commentMapper.selectVOByPostId(postId);
    }

    @Override
    public List<CommentVO> myComments(Long userId) {
        return commentMapper.selectVOByUserId(userId);
    }

    @Override
    public void delete(Long id, Long userId, boolean isAdmin) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在或已被删除");
        }
        // 权限校验：非管理员只能删除自己发布的评论
        if (!isAdmin && !comment.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己发布的评论");
        }
        commentMapper.deleteById(id);
    }

    @Override
    public List<CommentVO> listAll(String keyword) {
        return commentMapper.selectVOAll(keyword);
    }

    @Override
    public Long countAll() {
        return commentMapper.countAll();
    }
}
