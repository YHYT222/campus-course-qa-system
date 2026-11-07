package com.campus.qa.controller;

import com.campus.qa.dto.CommentVO;
import com.campus.qa.dto.Result;
import com.campus.qa.entity.Comment;
import com.campus.qa.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 回复评论接口 —— 对应需求 4.1.3 问答交互、4.1.4 个人中心
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    /** 查询某帖子的全部评论：GET /api/comment/post/{postId} */
    @GetMapping("/post/{postId}")
    public Result<List<CommentVO>> listByPost(@PathVariable Long postId) {
        return Result.ok(commentService.listByPost(postId));
    }

    /** 发表评论：POST /api/comment */
    @PostMapping("")
    public Result<Long> add(@RequestBody Comment comment, HttpServletRequest request) {
        Long id = commentService.add(comment.getPostId(), getUserId(request), comment.getContent());
        return Result.ok("回复成功", id);
    }

    /** 删除评论：DELETE /api/comment/{id}（学生仅可删除自己的） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        commentService.delete(id, getUserId(request), isAdmin(request));
        return Result.ok("删除成功", null);
    }

    /** 我的回复：GET /api/comment/my */
    @GetMapping("/my")
    public Result<List<CommentVO>> my(HttpServletRequest request) {
        return Result.ok(commentService.myComments(getUserId(request)));
    }
}
