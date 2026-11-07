package com.campus.qa.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.qa.common.BusinessException;
import com.campus.qa.common.Result;
import com.campus.qa.common.UserContext;
import com.campus.qa.entity.Favorite;
import com.campus.qa.entity.Post;
import com.campus.qa.entity.PostLike;
import com.campus.qa.entity.Reply;
import com.campus.qa.mapper.FavoriteMapper;
import com.campus.qa.mapper.PostLikeMapper;
import com.campus.qa.mapper.PostMapper;
import com.campus.qa.mapper.ReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 问答互动：点赞、收藏、评论回复
 */
@RestController
@RequestMapping("/api/interact")
public class InteractionController {

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ReplyMapper replyMapper;

    @Autowired
    private PostController postController;

    /**
     * 点赞 / 取消点赞（同一用户同一帖子仅一条记录，防刷赞）
     */
    @PostMapping("/like/{postId}")
    public Result<Map<String, Object>> toggleLike(@PathVariable Long postId) {
        Post post = requirePost(postId);
        Long userId = UserContext.get();

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
        data.put("likeCount", latest.getLikeCount());
        return Result.ok(data);
    }

    /**
     * 收藏 / 取消收藏
     */
    @PostMapping("/favorite/{postId}")
    public Result<Map<String, Object>> toggleFavorite(@PathVariable Long postId) {
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
        return Result.ok(Map.of("favorited", favorited));
    }

    /**
     * 我的收藏列表
     */
    @GetMapping("/favorites")
    public Result<List<Map<String, Object>>> favorites() {
        return Result.ok(favoriteMapper.listFavorites(UserContext.get()));
    }

    /**
     * 发表回复
     */
    @PostMapping("/reply")
    public Result<Void> reply(@RequestBody Map<String, String> body) {
        postController.checkNotMuted();

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
        reply.setUserId(UserContext.get());
        reply.setContent(content);
        replyMapper.insert(reply);
        return Result.ok();
    }

    /**
     * 某帖子的回复列表
     */
    @GetMapping("/replies/{postId}")
    public Result<List<Map<String, Object>>> replies(@PathVariable Long postId) {
        return Result.ok(replyMapper.listByPost(postId));
    }

    /**
     * 我的回复
     */
    @GetMapping("/my-replies")
    public Result<List<Map<String, Object>>> myReplies() {
        return Result.ok(replyMapper.listMyReplies(UserContext.get()));
    }

    private Post requirePost(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        return post;
    }
}
