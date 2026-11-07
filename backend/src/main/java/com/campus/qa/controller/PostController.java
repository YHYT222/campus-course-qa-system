package com.campus.qa.controller;

import com.campus.qa.dto.PageResult;
import com.campus.qa.dto.PostVO;
import com.campus.qa.dto.Result;
import com.campus.qa.entity.Post;
import com.campus.qa.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 答疑帖子接口 —— 对应需求 4.1.2 发帖、4.1.3 问答交互、4.1.4 个人中心
 */
@RestController
@RequestMapping("/api/post")
public class PostController extends BaseController {

    @Autowired
    private PostService postService;

    /** 发布帖子：POST /api/post （提交后状态为待审核） */
    @PostMapping("")
    public Result<Long> publish(@RequestBody Post post, HttpServletRequest request) {
        // 需求 3.2：管理员专注平台维护，不参与发帖
        if (isAdmin(request)) {
            return Result.fail("管理员账号不可发布答疑帖子");
        }
        Long id = postService.publish(getUserId(request), post.getCategoryId(),
                post.getContent(), post.getImage());
        return Result.ok("发布成功，请等待管理员审核", id);
    }

    /** 首页帖子列表（分页 + 课程筛选 + 关键词搜索）：GET /api/post/list */
    @GetMapping("/list")
    public Result<PageResult<PostVO>> list(@RequestParam(required = false) Integer pageNum,
                                           @RequestParam(required = false) Integer pageSize,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) String keyword,
                                           HttpServletRequest request) {
        PageResult<PostVO> page = postService.page(pageNum, pageSize, categoryId, keyword, getUserId(request));
        return Result.ok(page);
    }

    /** 帖子详情：GET /api/post/{id} */
    @GetMapping("/{id}")
    public Result<PostVO> detail(@PathVariable Long id, HttpServletRequest request) {
        return Result.ok(postService.detail(id, getUserId(request)));
    }

    /** 我的提问：GET /api/post/my */
    @GetMapping("/my")
    public Result<List<PostVO>> my(HttpServletRequest request) {
        return Result.ok(postService.myPosts(getUserId(request)));
    }

    /** 删除帖子：DELETE /api/post/{id}（学生仅可删除自己的） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        postService.delete(id, getUserId(request), isAdmin(request));
        return Result.ok("删除成功", null);
    }

    /** 点赞：POST /api/post/like/{id} */
    @PostMapping("/like/{id}")
    public Result<Void> like(@PathVariable Long id, HttpServletRequest request) {
        postService.like(id, getUserId(request));
        return Result.ok("点赞成功", null);
    }

    /** 取消点赞：DELETE /api/post/like/{id} */
    @DeleteMapping("/like/{id}")
    public Result<Void> unlike(@PathVariable Long id, HttpServletRequest request) {
        postService.unlike(id, getUserId(request));
        return Result.ok("已取消点赞", null);
    }

    /** 收藏：POST /api/post/favorite/{id} */
    @PostMapping("/favorite/{id}")
    public Result<Void> favorite(@PathVariable Long id, HttpServletRequest request) {
        postService.favorite(id, getUserId(request));
        return Result.ok("收藏成功", null);
    }

    /** 取消收藏：DELETE /api/post/favorite/{id} */
    @DeleteMapping("/favorite/{id}")
    public Result<Void> unfavorite(@PathVariable Long id, HttpServletRequest request) {
        postService.unfavorite(id, getUserId(request));
        return Result.ok("已取消收藏", null);
    }

    /** 我的收藏：GET /api/post/favorite */
    @GetMapping("/favorite")
    public Result<List<PostVO>> favorites(HttpServletRequest request) {
        return Result.ok(postService.favorites(getUserId(request)));
    }
}
