package com.myweb.mapper;

import com.myweb.model.Music;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MusicMapper {

    Music selectById(@Param("id") Long id);

    List<Music> selectList();

    int insert(Music music);

    int updateById(Music music);

    int deleteById(@Param("id") Long id);
}
