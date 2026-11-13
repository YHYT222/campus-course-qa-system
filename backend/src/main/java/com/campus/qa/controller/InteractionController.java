package com.campus.qa.controller;

import com.campus.qa.common.Result;
import com.campus.qa.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 问答互动：点赞、收藏、评论回复
 */
@RestController
@RequestMapping("/api/interact")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    /**
     * 点赞 / 取消点赞（同一用户同一帖子仅一条记录，防刷赞）
     */
    @PostMapping("/like/{postId}")
    public Result<Map<String, Object>> toggleLike(@PathVariable Long postId) {
        return Result.ok(interactionService.toggleLike(postId));
    }

    /**
     * 收藏 / 取消收藏
     */
    @PostMapping("/favorite/{postId}")
    public Result<Map<String, Object>> toggleFavorite(@PathVariable Long postId) {
        return Result.ok(interactionService.toggleFavorite(postId));
    }

    /**
     * 我的收藏列表
     */
    @GetMapping("/favorites")
    public Result<List<Map<String, Object>>> favorites() {
        return Result.ok(interactionService.favorites());
    }

    /**
     * 发表回复
     */
    @PostMapping("/reply")
    public Result<Void> reply(@RequestBody Map<String, String> body) {
        interactionService.addReply(body);
        return Result.ok();
    }

    /**
     * 某帖子的回复列表
     */
    @GetMapping("/replies/{postId}")
    public Result<List<Map<String, Object>>> replies(@PathVariable Long postId) {
        return Result.ok(interactionService.replies(postId));
    }

    /**
     * 我的回复
     */
    @GetMapping("/my-replies")
    public Result<List<Map<String, Object>>> myReplies() {
        return Result.ok(interactionService.myReplies());
    }

    /**
     * 删除自己发表的回复（仅限本人）
     */
    @DeleteMapping("/reply/{id}")
    public Result<Void> deleteReply(@PathVariable Long id) {
        interactionService.deleteReply(id);
        return Result.ok();
    }
}
