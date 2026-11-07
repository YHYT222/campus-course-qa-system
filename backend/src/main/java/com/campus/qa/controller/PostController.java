package com.campus.qa.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.qa.common.BusinessException;
import com.campus.qa.common.Result;
import com.campus.qa.common.UserContext;
import com.campus.qa.entity.Course;
import com.campus.qa.entity.Post;
import com.campus.qa.entity.User;
import com.campus.qa.mapper.CourseMapper;
import com.campus.qa.mapper.PostLikeMapper;
import com.campus.qa.mapper.FavoriteMapper;
import com.campus.qa.mapper.PostMapper;
import com.campus.qa.mapper.ReplyMapper;
import com.campus.qa.mapper.UserMapper;
import com.campus.qa.entity.PostLike;
import com.campus.qa.entity.Favorite;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 答疑帖子（学生端）
 */
@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ReplyMapper replyMapper;

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    /**
     * 分页浏览已发布帖子（支持课程筛选 + 关键词搜索）
     */
    @GetMapping("/page")
    public Result<IPage<Map<String, Object>>> page(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestParam(required = false) Long courseId,
                                                   @RequestParam(required = false) String keyword) {
        return Result.ok(postMapper.pagePublished(
                new Page<>(page, size), courseId, blankToNull(keyword)));
    }

    /**
     * 帖子详情（含回复列表、当前用户点赞/收藏状态）
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Map<String, Object> post = postMapper.selectDetailById(id);
        if (post == null) {
            throw new BusinessException("帖子不存在或已被删除");
        }
        if (Integer.valueOf(2).equals(post.get("status"))) {
            throw new BusinessException("该帖子已被屏蔽");
        }

        Map<String, Object> data = new HashMap<>(post);
        data.put("replies", replyMapper.listByPost(id));

        Long userId = UserContext.get();
        User current = userMapper.selectById(userId);
        data.put("isOwner", userId != null && userId.equals(post.get("userId")));
        data.put("liked", postLikeMapper.selectCount(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getUserId, userId).eq(PostLike::getPostId, id)) > 0);
        data.put("favorited", favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getPostId, id)) > 0);
        data.put("isAdmin", current != null && current.getRole() == User.ROLE_ADMIN);
        return Result.ok(data);
    }

    /**
     * 发布答疑帖
     */
    @PostMapping
    public Result<Void> publish(@RequestBody Map<String, Object> body) {
        checkNotMuted();

        Long courseId = toLong(body.get("courseId"));
        String title = trim((String) body.get("title"));
        String content = trim((String) body.get("content"));
        String image = trim((String) body.get("image"));

        if (courseId == null || courseMapper.selectById(courseId) == null) {
            throw new BusinessException("请选择有效的课程分类");
        }
        if (!StringUtils.hasText(title) || !StringUtils.hasText(content)) {
            throw new BusinessException("标题和问题描述不能为空");
        }
        if (title.length() > 100) {
            throw new BusinessException("标题不能超过100字");
        }

        Post post = new Post();
        post.setUserId(UserContext.get());
        post.setCourseId(courseId);
        post.setTitle(title);
        post.setContent(content);
        post.setImage(image);
        post.setLikeCount(0);
        post.setStatus(Post.STATUS_PUBLISHED);
        postMapper.insert(post);
        return Result.ok();
    }

    /**
     * 我的提问
     */
    @GetMapping("/my")
    public Result<List<Map<String, Object>>> myPosts() {
        return Result.ok(postMapper.listMyPosts(UserContext.get()));
    }

    /**
     * 删除自己的帖子
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMyPost(@PathVariable Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        User current = userMapper.selectById(UserContext.get());
        boolean owner = post.getUserId().equals(UserContext.get());
        boolean admin = current != null && current.getRole() == User.ROLE_ADMIN;
        if (!owner && !admin) {
            throw new BusinessException("只能删除自己的帖子");
        }
        deletePostCascade(id);
        return Result.ok();
    }

    /** 级联删除帖子的回复、点赞、收藏记录 */
    public void deletePostCascade(Long postId) {
        replyMapper.delete(new LambdaQueryWrapper<com.campus.qa.entity.Reply>()
                .eq(com.campus.qa.entity.Reply::getPostId, postId));
        postLikeMapper.delete(new LambdaQueryWrapper<PostLike>().eq(PostLike::getPostId, postId));
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>().eq(Favorite::getPostId, postId));
        postMapper.deleteById(postId);
    }

    /** 禁言用户校验 */
    public void checkNotMuted() {
        User user = userMapper.selectById(UserContext.get());
        if (user != null && user.getStatus() == User.STATUS_MUTED) {
            throw new BusinessException("您已被禁言，暂时无法发布内容和回复");
        }
    }

    private static Long toLong(Object o) {
        if (o == null) return null;
        try {
            return Long.parseLong(String.valueOf(o));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static String blankToNull(String s) {
        return StringUtils.hasText(s) ? s.trim() : null;
    }
}
