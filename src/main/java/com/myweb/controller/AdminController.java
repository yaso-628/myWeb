package com.myweb.controller;

import com.myweb.dto.Result;
import com.myweb.model.*;
import com.myweb.service.*;
import com.myweb.security.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 后台管理相关接口（需 JWT 认证）
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final BlogService blogService;
    private final CommentService commentService;
    private final LinkService linkService;
    private final ProfileService profileService;
    private final MusicService musicService;

    /**
     * 保存上传文件到本地 uploads 目录，并返回可访问的 URL
     */
    private String storeFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("文件为空");
        }
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.lastIndexOf('.') >= 0) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String filename = System.currentTimeMillis() + "-" + UUID.randomUUID() + ext;
        Path uploadDir = Paths.get("uploads", subDir).toAbsolutePath();
        Files.createDirectories(uploadDir);
        Path target = uploadDir.resolve(filename);
        file.transferTo(target.toFile());
        // 返回给前端访问的 URL 路径
        return "/uploads/" + subDir + "/" + filename;
    }

    @GetMapping("/stats")
    public Result<Map<String, Long>> stats() {
        Map<String, Long> data = new HashMap<>();
        data.put("blogCount", adminService.countBlogs(null));
        data.put("commentCount", adminService.countComments(null));
        data.put("linkCount", adminService.countLinks(null));
        return Result.ok(data);
    }

    @GetMapping("/blogs/{id}")
    public Result<Blog> getBlog(@PathVariable Long id) {
        Blog blog = blogService.getById(id);
        if (blog == null) return Result.fail(404, "文章不存在");
        return Result.ok(blog);
    }

    @GetMapping("/blogs")
    public Result<Map<String, Object>> listBlogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        List<Blog> list = adminService.listBlogs(page, size, status);
        long total = adminService.countBlogs(status);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return Result.ok(data);
    }

    @PostMapping("/blogs")
    public Result<Long> createBlog(@RequestBody Blog blog) {
        if (blog.getTitle() == null || blog.getTitle().isBlank()) {
            return Result.fail(400, "标题不能为空");
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal principal) {
            blog.setAuthorId(principal.getUser().getId());
        }
        return Result.ok(blogService.save(blog));
    }

    @PutMapping("/blogs/{id}")
    public Result<Void> updateBlog(@PathVariable Long id, @RequestBody Blog blog) {
        if (blog.getTitle() == null || blog.getTitle().isBlank()) {
            return Result.fail(400, "标题不能为空");
        }
        blog.setId(id);
        blogService.update(blog);
        return Result.ok();
    }

    @DeleteMapping("/blogs/{id}")
    public Result<Void> deleteBlog(@PathVariable Long id) {
        blogService.deleteById(id);
        return Result.ok();
    }

    @GetMapping("/comments")
    public Result<Map<String, Object>> listComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long blogId,
            @RequestParam(required = false) Boolean guestbookOnly,
            @RequestParam(required = false) Integer status) {
        List<Comment> list;
        long total;
        if (Boolean.TRUE.equals(guestbookOnly)) {
            list = commentService.listGuestbook(page, size, status);
            total = commentService.countGuestbook(status);
        } else {
            list = adminService.listComments(page, size, blogId, status);
            total = commentService.countList(blogId, status);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return Result.ok(data);
    }

    @PutMapping("/comments/{id}/status")
    public Result<Void> updateCommentStatus(@PathVariable Long id, @RequestParam Integer status) {
        commentService.updateStatus(id, status);
        return Result.ok();
    }

    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteById(id);
        return Result.ok();
    }

    @GetMapping("/links")
    public Result<Map<String, Object>> listLinks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        List<Link> list = adminService.listLinks(page, size, status);
        long total = adminService.countLinks(status);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        return Result.ok(data);
    }

    @PostMapping("/links")
    public Result<Long> createLink(@RequestBody Link link) {
        return Result.ok(linkService.save(link));
    }

    @PutMapping("/links/{id}")
    public Result<Void> updateLink(@PathVariable Long id, @RequestBody Link link) {
        link.setId(id);
        linkService.update(link);
        return Result.ok();
    }

    @PutMapping("/links/{id}/status")
    public Result<Void> updateLinkStatus(@PathVariable Long id, @RequestParam Integer status) {
        linkService.updateStatus(id, status);
        return Result.ok();
    }

    @DeleteMapping("/links/{id}")
    public Result<Void> deleteLink(@PathVariable Long id) {
        linkService.deleteById(id);
        return Result.ok();
    }

    @GetMapping("/profile")
    public Result<List<Profile>> listProfile() {
        return Result.ok(profileService.listAll());
    }

    @PostMapping("/profile")
    public Result<Void> saveProfile(@RequestBody Profile profile) {
        profileService.saveOrUpdate(profile);
        return Result.ok();
    }

    /**
     * 上传背景图文件，并自动更新 bg_image 配置
     */
    @PostMapping("/upload/bg-image")
    public Result<String> uploadBgImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = storeFile(file, "bg");
            Profile profile = new Profile();
            profile.setKeyName("bg_image");
            profile.setValue(url);
            profile.setType("text");
            profileService.saveOrUpdate(profile);
            return Result.ok(url);
        } catch (IOException e) {
            return Result.fail(500, "背景图上传失败");
        }
    }

    /**
     * 上传背景音乐文件，并自动更新 bgm_url 配置（兼容旧逻辑）
     */
    @PostMapping("/upload/bgm")
    public Result<String> uploadBgm(@RequestParam("file") MultipartFile file) {
        try {
            String url = storeFile(file, "bgm");
            Profile profile = new Profile();
            profile.setKeyName("bgm_url");
            profile.setValue(url);
            profile.setType("text");
            profileService.saveOrUpdate(profile);
            return Result.ok(url);
        } catch (IOException e) {
            return Result.fail(500, "背景音乐上传失败");
        }
    }

    /** 音乐列表 CRUD */
    @GetMapping("/music")
    public Result<List<Music>> listMusic() {
        return Result.ok(musicService.list());
    }

    @PostMapping("/music")
    public Result<Long> addMusic(@RequestBody Music music) {
        if (music.getTitle() == null || music.getTitle().isBlank()) {
            return Result.fail(400, "歌名不能为空");
        }
        if (music.getUrl() == null || music.getUrl().isBlank()) {
            return Result.fail(400, "音乐链接不能为空");
        }
        return Result.ok(musicService.save(music));
    }

    @PutMapping("/music/{id}")
    public Result<Void> updateMusic(@PathVariable Long id, @RequestBody Music music) {
        music.setId(id);
        musicService.update(music);
        return Result.ok();
    }

    @DeleteMapping("/music/{id}")
    public Result<Void> deleteMusic(@PathVariable Long id) {
        musicService.deleteById(id);
        return Result.ok();
    }

    /** 上传音乐文件，返回 URL（用于添加到音乐列表） */
    @PostMapping("/music/upload")
    public Result<String> uploadMusic(@RequestParam("file") MultipartFile file) {
        try {
            return Result.ok(storeFile(file, "bgm"));
        } catch (IOException e) {
            return Result.fail(500, "音乐上传失败");
        }
    }

    @GetMapping("/users")
    public Result<List<User>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(adminService.listUsers(page, size));
    }
}
