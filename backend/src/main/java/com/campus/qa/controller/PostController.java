package com.campus.qa.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.qa.common.Result;
import com.campus.qa.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 答疑帖子（学生端）
 * Controller 只负责接收参数、调用 Service、封装统一返回
 */
@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 分页浏览已发布帖子（支持课程筛选 + 关键词搜索）
     */
    @GetMapping("/page")
    public Result<IPage<Map<String, Object>>> page(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestParam(required = false) Long courseId,
                                                   @RequestParam(required = false) String keyword) {
        return Result.ok(postService.page(page, size, courseId, keyword));
    }

    /**
     * 帖子详情（含回复列表、当前用户点赞/收藏状态）
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        return Result.ok(postService.detail(id));
    }

    /**
     * 发布答疑帖（提交后进入待审核状态）
     */
    @PostMapping
    public Result<Void> publish(@RequestBody Map<String, Object> body) {
        postService.publish(body);
        return Result.ok();
    }

    /**
     * 我的提问
     */
    @GetMapping("/my")
    public Result<List<Map<String, Object>>> myPosts() {
        return Result.ok(postService.myPosts());
    }

    /**
     * 删除自己的帖子
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMyPost(@PathVariable Long id) {
        postService.deleteMyPost(id);
        return Result.ok();
    }
}
