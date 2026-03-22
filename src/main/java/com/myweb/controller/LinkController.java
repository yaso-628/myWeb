package com.myweb.controller;

import com.myweb.dto.Result;
import com.myweb.model.Link;
import com.myweb.security.JwtUserPrincipal;
import com.myweb.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 友链相关接口（前台只读已通过）
 */
@RestController
@RequestMapping("/api/link")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    /**
     * 普通用户申请友链：创建为待审核状态，管理员在后台审核后展示
     */
    @PostMapping("/apply")
    public Result<Long> apply(@RequestBody Link link) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal)) {
            return Result.fail(401, "请先登录后再申请友链");
        }
        if (link.getName() == null || link.getName().isBlank()) {
            return Result.fail(400, "友链名称不能为空");
        }
        if (link.getUrl() == null || link.getUrl().isBlank()) {
            return Result.fail(400, "友链地址不能为空");
        }
        // 状态留空或显式置为 0，统一走待审核流程
        link.setStatus(0);
        Long id = linkService.save(link);
        return Result.ok(id);
    }

    @GetMapping("/list")
    public Result<List<Link>> list(@RequestParam(defaultValue = "1") Integer status) {
        return Result.ok(linkService.list(status));
    }

    @GetMapping("/{id}")
    public Result<Link> getById(@PathVariable Long id) {
        Link link = linkService.getById(id);
        return link == null ? Result.fail(404, "友链不存在") : Result.ok(link);
    }
}
