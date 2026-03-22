package com.myweb.controller;

import com.myweb.dto.PersonalProfileDTO;
import com.myweb.dto.Result;
import com.myweb.model.Profile;
import com.myweb.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 个人简介/站点配置相关接口
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /** 获取个人信息（头像、简介、联系方式） */
    @GetMapping("/info")
    public Result<PersonalProfileDTO> getPersonalInfo() {
        return Result.ok(profileService.getPersonalInfo());
    }

    /** 更新个人信息（支持部分更新，只传需要修改的字段即可） */
    @PutMapping("/info")
    public Result<Void> updatePersonalInfo(@RequestBody PersonalProfileDTO dto) {
        profileService.updatePersonalInfo(dto);
        return Result.ok();
    }

    @GetMapping("/{key}")
    public Result<Profile> getByKey(@PathVariable String key) {
        Profile profile = profileService.getByKey(key);
        return profile == null ? Result.fail(404, "配置不存在") : Result.ok(profile);
    }

    @GetMapping("/all")
    public Result<List<Profile>> listAll() {
        return Result.ok(profileService.listAll());
    }
}
