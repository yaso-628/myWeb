package com.myweb.controller;

import com.myweb.dto.Result;
import com.myweb.model.Blog;
import com.myweb.security.JwtUserPrincipal;
import com.myweb.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 博客文章相关接口（前台）
 */
@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping("/{id}")
    public Result<Blog> getById(@PathVariable Long id) {
        Blog blog = blogService.getById(id);
        if (blog == null) {
            return Result.fail(404, "文章不存在");
        }
        blogService.incrementViewCount(id);
        blog.setViewCount(blog.getViewCount() == null ? 1 : blog.getViewCount() + 1);
        return Result.ok(blog);
    }

    /**
     * 普通用户投稿文章：创建为草稿/待审核状态，由管理员在后台审核后再发布
     */
    @PostMapping("/submit")
    public Result<Long> submit(@RequestBody Blog blog) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return Result.fail(401, "请先登录后再投稿");
        }
        if (blog.getTitle() == null || blog.getTitle().isBlank()) {
            return Result.fail(400, "标题不能为空");
        }
        blog.setAuthorId(principal.getUser().getId());
        blog.setStatus(0); // 0-草稿/待审核，仅管理员审核发布后前台可见
        Long id = blogService.save(blog);
        return Result.ok(id);
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String category) {
        if (status == null) status = 1;
        List<Blog> list = blogService.list(page, size, status, category);
        long total = blogService.count(status, category);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return Result.ok(data);
    }
}
