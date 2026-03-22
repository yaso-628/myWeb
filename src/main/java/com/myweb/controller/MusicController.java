package com.myweb.controller;

import com.myweb.dto.Result;
import com.myweb.model.Music;
import com.myweb.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 音乐列表接口（公开访问）
 */
@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @GetMapping
    public Result<List<Music>> list() {
        return Result.ok(musicService.list());
    }
}
