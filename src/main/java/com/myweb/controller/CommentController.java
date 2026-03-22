package com.myweb.controller;

import com.myweb.dto.Result;
import com.myweb.model.Comment;
import com.myweb.security.JwtUserPrincipal;
import com.myweb.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 留言板/评论相关接口（前台）
 */
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/blog/{blogId}")
    public Result<List<Comment>> listByBlogId(
            @PathVariable Long blogId,
            @RequestParam(defaultValue = "1") Integer status) {
        return Result.ok(commentService.listByBlogId(blogId, status));
    }

    @GetMapping("/guestbook")
    public Result<Map<String, Object>> guestbook(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "1") Integer status) {
        List<Comment> list = commentService.listGuestbook(page, size, status);
        long total = commentService.countGuestbook(status);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return Result.ok(data);
    }

    /**
     * 新增评论/留言：
     * - 未登录用户需提供昵称和邮箱
     * - 登录用户自动使用账号昵称和邮箱，并绑定 userId，便于在评论区展示个人信息
     */
    @PostMapping
    public Result<Long> add(@RequestBody Comment comment, HttpServletRequest request) {
        if (comment.getContent() == null || comment.getContent().isBlank()) {
            return Result.fail(400, "评论内容不能为空");
        }
        // 顶级评论时 parentId 视为 0
        if (comment.getParentId() == null) {
            comment.setParentId(0L);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal principal) {
            // 登录用户：绑定 userId，并使用账号上的昵称和邮箱
            var user = principal.getUser();
            comment.setUserId(user.getId());
            comment.setNickname(user.getNickname());
            comment.setEmail(user.getEmail());
        } else {
            // 未登录：要求传入昵称和邮箱，便于展示和审核
            if (comment.getNickname() == null || comment.getNickname().isBlank()) {
                return Result.fail(400, "昵称不能为空");
            }
            if (comment.getEmail() == null || comment.getEmail().isBlank()) {
                return Result.fail(400, "邮箱不能为空");
            }
        }

        // 记录提交 IP，方便后台管理（可选）
        if (comment.getIp() == null || comment.getIp().isBlank()) {
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isBlank()) {
                ip = request.getRemoteAddr();
            } else {
                // 多级代理时取第一个
                int idx = ip.indexOf(',');
                if (idx > 0) {
                    ip = ip.substring(0, idx).trim();
                }
            }
            comment.setIp(ip);
        }

        Long id = commentService.add(comment);
        return Result.ok(id);
    }
}
