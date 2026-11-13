package com.campus.qa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.qa.common.BusinessException;
import com.campus.qa.common.UserContext;
import com.campus.qa.entity.Favorite;
import com.campus.qa.entity.Post;
import com.campus.qa.entity.PostLike;
import com.campus.qa.entity.Reply;
import com.campus.qa.entity.User;
import com.campus.qa.mapper.CourseMapper;
import com.campus.qa.mapper.FavoriteMapper;
import com.campus.qa.mapper.PostLikeMapper;
import com.campus.qa.mapper.PostMapper;
import com.campus.qa.mapper.ReplyMapper;
import com.campus.qa.mapper.UserMapper;
import com.campus.qa.service.PostService;
import com.campus.qa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 答疑帖子业务实现
 */
@Service
public class PostServiceImpl implements PostService {

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

    @Autowired
    private UserService userService;

    @Override
    public IPage<Map<String, Object>> page(Integer page, Integer size, Long courseId, String keyword) {
        return postMapper.pagePublished(new Page<>(page, size), courseId, blankToNull(keyword));
    }

    @Override
    public Map<String, Object> detail(Long id) {
        Map<String, Object> post = postMapper.selectDetailById(id);
        if (post == null) {
            throw new BusinessException("帖子不存在或已被删除");
        }
        if (Integer.valueOf(Post.STATUS_BLOCKED).equals(post.get("status"))) {
            throw new BusinessException("该帖子已被屏蔽");
        }

        Map<String, Object> data = new HashMap<>(post);
        data.put("replies", replyMapper.listByPost(id));

        Long userId = UserContext.get();
        User current = userId == null ? null : userMapper.selectById(userId);
        data.put("isOwner", userId != null && userId.equals(post.get("userId")));
        data.put("liked", userId != null && postLikeMapper.selectCount(
                new LambdaQueryWrapper<PostLike>()
                        .eq(PostLike::getUserId, userId).eq(PostLike::getPostId, id)) > 0);
        data.put("favorited", userId != null && favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId).eq(Favorite::getPostId, id)) > 0);
        data.put("isAdmin", current != null && current.getRole() == User.ROLE_ADMIN);
        return data;
    }

    @Override
    public void publish(Map<String, Object> body) {
        Long userId = UserContext.get();
        userService.checkNotMuted(userId);

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
        post.setUserId(userId);
        post.setCourseId(courseId);
        post.setTitle(title);
        post.setContent(content);
        post.setImage(image);
        post.setLikeCount(0);
        // 业务规则：新帖一律进入待审核状态，须管理员审核通过后才对学生端可见
        post.setStatus(Post.STATUS_PENDING);
        postMapper.insert(post);
    }

    @Override
    public List<Map<String, Object>> myPosts() {
        return postMapper.listMyPosts(UserContext.get());
    }

    @Override
    public void deleteMyPost(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        Long userId = UserContext.get();
        User current = userMapper.selectById(userId);
        boolean owner = post.getUserId().equals(userId);
        boolean admin = current != null && current.getRole() == User.ROLE_ADMIN;
        if (!owner && !admin) {
            throw new BusinessException("只能删除自己的帖子");
        }
        deleteCascade(id);
    }

    @Override
    public void deleteCascade(Long postId) {
        replyMapper.delete(new LambdaQueryWrapper<Reply>().eq(Reply::getPostId, postId));
        postLikeMapper.delete(new LambdaQueryWrapper<PostLike>().eq(PostLike::getPostId, postId));
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>().eq(Favorite::getPostId, postId));
        postMapper.deleteById(postId);
    }

    private static Long toLong(Object o) {
        if (o == null) {
            return null;
        }
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
