package com.myweb.service;

import com.myweb.model.Music;

import java.util.List;

public interface MusicService {

    List<Music> list();

    Music getById(Long id);

    Long save(Music music);

    void update(Music music);

    void deleteById(Long id);
}
