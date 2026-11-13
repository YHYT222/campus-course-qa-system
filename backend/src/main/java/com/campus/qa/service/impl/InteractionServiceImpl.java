package com.campus.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.qa.common.BusinessException;
import com.campus.qa.common.UserContext;
import com.campus.qa.entity.Favorite;
import com.campus.qa.entity.Post;
import com.campus.qa.entity.PostLike;
import com.campus.qa.entity.Reply;
import com.campus.qa.mapper.FavoriteMapper;
import com.campus.qa.mapper.PostLikeMapper;
import com.campus.qa.mapper.PostMapper;
import com.campus.qa.mapper.ReplyMapper;
import com.campus.qa.service.InteractionService;
import com.campus.qa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 互动业务实现
 */
@Service
public class InteractionServiceImpl implements InteractionService {

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ReplyMapper replyMapper;

    @Autowired
    private UserService userService;

    @Override
    public Map<String, Object> toggleLike(Long postId) {
        requirePost(postId);
        Long userId = UserContext.get();

        // 同一用户同一帖子只保留一条点赞记录，再次点击即为取消
        PostLike exist = postLikeMapper.selectOne(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getUserId, userId).eq(PostLike::getPostId, postId));

        boolean liked;
        if (exist != null) {
            postLikeMapper.deleteById(exist.getId());
            postMapper.update(null, new LambdaUpdateWrapper<Post>()
                    .eq(Post::getId, postId)
                    .setSql("like_count = GREATEST(like_count - 1, 0)"));
            liked = false;
        } else {
            PostLike like = new PostLike();
            like.setUserId(userId);
            like.setPostId(postId);
            postLikeMapper.insert(like);
            postMapper.update(null, new LambdaUpdateWrapper<Post>()
                    .eq(Post::getId, postId)
                    .setSql("like_count = like_count + 1"));
            liked = true;
        }

        Post latest = postMapper.selectById(postId);
        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        data.put("likeCount", latest == null ? 0 : latest.getLikeCount());
        return data;
    }

    @Override
    public Map<String, Object> toggleFavorite(Long postId) {
        requirePost(postId);
        Long userId = UserContext.get();

        Favorite exist = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getPostId, postId));

        boolean favorited;
        if (exist != null) {
            favoriteMapper.deleteById(exist.getId());
            favorited = false;
        } else {
            Favorite fav = new Favorite();
            fav.setUserId(userId);
            fav.setPostId(postId);
            favoriteMapper.insert(fav);
            favorited = true;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("favorited", favorited);
        return data;
    }

    @Override
    public List<Map<String, Object>> favorites() {
        return favoriteMapper.listFavorites(UserContext.get());
    }

    @Override
    public void addReply(Map<String, String> body) {
        Long userId = UserContext.get();
        userService.checkNotMuted(userId);

        Long postId = null;
        try {
            postId = Long.parseLong(body.get("postId"));
        } catch (Exception ignored) {
        }
        String content = body.get("content") == null ? null : body.get("content").trim();

        if (postId == null || postMapper.selectById(postId) == null) {
            throw new BusinessException("帖子不存在");
        }
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("回复内容不能为空");
        }

        Reply reply = new Reply();
        reply.setPostId(postId);
        reply.setUserId(userId);
        reply.setContent(content);
        replyMapper.insert(reply);
    }

    @Override
    public List<Map<String, Object>> replies(Long postId) {
        return replyMapper.listByPost(postId);
    }

    @Override
    public List<Map<String, Object>> myReplies() {
        return replyMapper.listMyReplies(UserContext.get());
    }

    @Override
    public void deleteReply(Long replyId) {
        Reply reply = replyMapper.selectById(replyId);
        if (reply == null) {
            throw new BusinessException("回复不存在或已被删除");
        }
        // 数据权限：只允许删除自己发表的回复
        if (!reply.getUserId().equals(UserContext.get())) {
            throw new BusinessException("只能删除自己的回复");
        }
        replyMapper.deleteById(replyId);
    }

    private Post requirePost(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        return post;
    }
}
