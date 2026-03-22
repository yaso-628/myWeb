package com.myweb.service.impl;

import com.myweb.mapper.MusicMapper;
import com.myweb.model.Music;
import com.myweb.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private final MusicMapper musicMapper;

    @Override
    public List<Music> list() {
        return musicMapper.selectList();
    }

    @Override
    public Music getById(Long id) {
        return musicMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(Music music) {
        music.setCreateTime(LocalDateTime.now());
        music.setUpdateTime(LocalDateTime.now());
        if (music.getSort() == null) music.setSort(0);
        if (music.getArtist() == null) music.setArtist("");
        musicMapper.insert(music);
        return music.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Music music) {
        music.setUpdateTime(LocalDateTime.now());
        musicMapper.updateById(music);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        musicMapper.deleteById(id);
    }
}
