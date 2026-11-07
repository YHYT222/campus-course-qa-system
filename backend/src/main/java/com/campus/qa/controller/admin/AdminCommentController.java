package com.campus.qa.controller.admin;

import com.campus.qa.dto.CommentVO;
import com.campus.qa.dto.Result;
import com.campus.qa.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员 - 评论内容管理接口
 * 用于清理灌水、违规评论，维护社区内容（需求 3.2、4.2.2 内容管理范畴）
 */
@RestController
@RequestMapping("/api/admin/comment")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    /** 评论列表：GET /api/admin/comment/list */
    @GetMapping("/list")
    public Result<List<CommentVO>> list(@RequestParam(required = false) String keyword) {
        return Result.ok(commentService.listAll(keyword));
    }

    /** 删除违规评论：DELETE /api/admin/comment/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 管理员删除时跳过归属校验
        commentService.delete(id, null, true);
        return Result.ok("删除成功", null);
    }
}
