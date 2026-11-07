package com.campus.qa.controller.admin;

import com.campus.qa.dto.PageResult;
import com.campus.qa.dto.PostVO;
import com.campus.qa.dto.Result;
import com.campus.qa.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员 - 帖子审核管理接口 —— 对应需求 4.2.2 帖子审核管理功能
 */
@RestController
@RequestMapping("/api/admin/post")
public class AdminPostController {

    @Autowired
    private PostService postService;

    /** 帖子列表（可按状态筛选）：GET /api/admin/post/list?status=0 */
    @GetMapping("/list")
    public Result<PageResult<PostVO>> list(@RequestParam(required = false) Integer pageNum,
                                           @RequestParam(required = false) Integer pageSize,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(required = false) String keyword) {
        return Result.ok(postService.adminPage(pageNum, pageSize, status, keyword));
    }

    /** 审核帖子：PUT /api/admin/post/audit/{id}?status=1  （1-通过 2-屏蔽） */
    @PutMapping("/audit/{id}")
    public Result<Void> audit(@PathVariable Long id, @RequestParam Integer status) {
        postService.audit(id, status);
        String msg = status != null && status == 1 ? "已通过审核，帖子公开展示" : "已屏蔽该帖子";
        return Result.ok(msg, null);
    }

    /** 删除帖子：DELETE /api/admin/post/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 管理员删除时跳过归属校验
        postService.delete(id, null, true);
        return Result.ok("删除成功", null);
    }
}
