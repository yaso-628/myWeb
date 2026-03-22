package com.myweb.controller;

import com.myweb.dto.Result;
import com.myweb.dto.UserProfileDTO;
import com.myweb.model.User;
import com.myweb.security.JwtUserPrincipal;
import com.myweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 登录用户个人信息接口
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    private static final long MAX_AVATAR_SIZE = 20L * 1024 * 1024; // 20MB

    /**
     * 获取当前登录用户的个人信息
     */
    @GetMapping("/me")
    public Result<UserProfileDTO> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return Result.fail(401, "未登录");
        }
        User u = principal.getUser();
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setNickname(u.getNickname());
        dto.setEmail(u.getEmail());
        dto.setAvatar(u.getAvatar());
        dto.setRole(u.getRole());
        return Result.ok(dto);
    }

    /**
     * 更新当前登录用户的个人信息（昵称、邮箱、头像）
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserProfileDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal principal)) {
            return Result.fail(401, "未登录");
        }
        User u = principal.getUser();
        boolean changed = false;
        if (dto.getNickname() != null) {
            u.setNickname(dto.getNickname());
            changed = true;
        }
        if (dto.getEmail() != null) {
            u.setEmail(dto.getEmail());
            changed = true;
        }
        if (dto.getAvatar() != null) {
            u.setAvatar(dto.getAvatar());
            changed = true;
        }
        if (!changed) {
            return Result.ok();
        }
        u.setUpdateTime(LocalDateTime.now());
        userService.updateProfile(u);
        return Result.ok();
    }

    /**
     * 上传头像图片，返回可访问的 URL，限制最大 20MB
     */
    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof JwtUserPrincipal)) {
            return Result.fail(401, "未登录");
        }
        if (file == null || file.isEmpty()) {
            return Result.fail(400, "文件不能为空");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            return Result.fail(400, "头像文件不能超过20MB");
        }
        try {
            String url = storeAvatarFile(file);
            return Result.ok(url);
        } catch (IOException e) {
            return Result.fail(500, "头像上传失败");
        }
    }

    private String storeAvatarFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.lastIndexOf('.') >= 0) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String filename = System.currentTimeMillis() + "-" + UUID.randomUUID() + ext;
        Path uploadDir = Paths.get("uploads", "avatar").toAbsolutePath();
        Files.createDirectories(uploadDir);
        Path target = uploadDir.resolve(filename);
        file.transferTo(target.toFile());
        return "/uploads/avatar/" + filename;
    }
}

